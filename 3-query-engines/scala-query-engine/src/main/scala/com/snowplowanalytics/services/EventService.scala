/*
 * Copyright (c) 2015 Snowplow Analytics Ltd. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package com.snowplowanalytics.services


// Java
import java.util.Date
import java.util.TimeZone
import java.text.SimpleDateFormat

import scala.collection.mutable.ArrayBuffer

// Scala
import awscala._
import awscala.dynamodbv2._
import awscala.dynamodbv2.GlobalSecondaryIndex
import com.amazonaws.services.{ dynamodbv2 => aws }
import spray.json._
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._


// package import
import com.snowplowanalytics.model.{DruidResponse, SimpleEventJsonProtocol, SimpleEvent, DruidRequest}
import com.snowplowanalytics.services.EventData._


/**
 * EventService Object holds all the functions for DynamoDB access
 */
object EventService {

  // sets DynamoDB client to us-east-1
  implicit val dynamoDB = DynamoDB.at(Region.US_EAST_1)

  // sets dynamodb table name
  val tablename = "my-table"

  // sets up table and dynamodb connection
  val table: Table = dynamoDB.table(tablename).get

  // sets various settings for global secondary index
  val dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  val timezone = TimeZone.getTimeZone("UTC")
  val Hash = aws.model.KeyType.HASH
  val Range = aws.model.KeyType.RANGE
  val Include = aws.model.ProjectionType.INCLUDE
  val All = aws.model.ProjectionType.ALL

  // sets up table access to global secondary index with read write 10
  val globalSecondaryIndex = GlobalSecondaryIndex(
    name = "CountsIndex",
    keySchema = Seq(KeySchema("EventType", Hash), KeySchema("Timestamp", Range)),
    projection = Projection(All),
    provisionedThroughput = ProvisionedThroughput(readCapacityUnits = 10, writeCapacityUnits = 10)
  )


  /**
   * Function timezone helper
   */
  def timeNow(): String = {
    dateFormatter.setTimeZone(timezone)
    dateFormatter.format(new Date())
  }

  /**
   * Function gets total count of all events that match eventType and time bucket
   */
  def getCountByEventTypeTimestamp(eventType: String, timestamp: String, table: Table): Seq[String] = {
    val findRedwithTimestamp: Seq[Item] = table.queryWithIndex(
      index = globalSecondaryIndex,
      keyConditions = Seq("EventType" -> cond.eq(eventType), "Timestamp" -> cond.eq(timestamp))
    )
    findRedwithTimestamp.flatMap(_.attributes.find(_.name == "Count").map(_.value.n.get))
  }

  /**
   * Function gets unique list of events that match time bucket
   * res2: Seq[String] = ArrayBuffer(Green, Blue, Red, Yellow)
   */
  def getListOfEventsByTimestamp(bucket: String, table: Table): Seq[String] = {
    val timestampResult: Seq[awscala.dynamodbv2.Item] = table.scan(Seq("Timestamp" -> cond.eq(bucket)))
    val attribsOfFourElements: Seq[Seq[awscala.dynamodbv2.Attribute]] = timestampResult.map(_.attributes)
    timestampResult.flatMap(_.attributes.find(_.name == "EventType").map(_.value.s.get))
  }


  /**
   * Function gets all events matching range between 2 time buckets in DynamoDB
   */
  def getEventsByBucketsBetweenTimestamps(startingTimestamp: String, endingTimestamp: String): List[SimpleEvent] = {
    val timestampResult: Seq[awscala.dynamodbv2.Item] = table.scan(Seq("Timestamp" -> cond.between(startingTimestamp, endingTimestamp)))
    val attribsOfElements: Seq[Seq[awscala.dynamodbv2.Attribute]] = timestampResult.map(_.attributes)
    convertDataStage(attribsOfElements).toList
  }


  /**
   * Function gets all events as Individual Items matching range between 2 time buckets in DynamoDB
   */
  def druidRequestIndividualItems(druidRequest: DruidRequest): String = {
    val intervals = druidRequest.intervals.split("/")
    val timestampResult: Seq[awscala.dynamodbv2.Item] = table.scan(Seq("Timestamp" -> cond.between(intervals(0), intervals(0))))
    val attribsOfElements: Seq[Seq[awscala.dynamodbv2.Attribute]] = timestampResult.map(_.attributes)
    serialize(convertDataStage(attribsOfElements).toList).toJson.toString
  }

  /**
   * Function gets all events matching to time bucket in DynamoDB
   */
  def getEventsByBucket(timestamp: String) {
    val pattern = """^([\+-]?\d{4}(?!\d{2}\b))((-?)((0[1-9]|1[0-2])(\3([12]\d|0[1-9]|3[01]))?|W([0-4]\d|5[0-2])(-?[1-7])?|(00[1-9]|0[1-9]\d|[12]\d{2}|3([0-5]\d|6[1-6])))([T\s]((([01]\d|2[0-3])((:?)[0-5]\d)?|24\:?00)([\.,]\d+(?!:))?)?(\17[0-5]\d([\.,]\d+)?)?([zZ]|([\+-])([01]\d|2[0-3]):?([0-5]\d)?)?)?)?$""".r
    val bucket = timestamp
    val timestampResult: Seq[awscala.dynamodbv2.Item] = table.scan(Seq("Timestamp" -> cond.eq(bucket)))
    val attribsOfElements: Seq[Seq[awscala.dynamodbv2.Attribute]] = timestampResult.map(_.attributes)
    serialize(convertDataStage(attribsOfElements).toList)
  }


  /**
   * Function gets all events matching range between 2 time buckets in DynamoDB
   * scala.collection.immutable.Iterable[spray.json.JsObject]
   */
  def druidRequest(druidRequest: DruidRequest): String = {
    val intervals = druidRequest.intervals.split("/")
    val timestampResult: Seq[awscala.dynamodbv2.Item] = table.scan(Seq("Timestamp" -> cond.between(intervals(0), intervals(1))))
    val attribsOfElements: Seq[Seq[awscala.dynamodbv2.Attribute]] = timestampResult.map(_.attributes)
    countDruidResponse(convertDataStage(attribsOfElements).toList).toJson.toString
  }


  /**
   * Helper Function for converting DynamoDB to SimpleEvent model
   */
  def convertDataStage(dynamoArray: Seq[Seq[awscala.dynamodbv2.Attribute]]): scala.collection.mutable.ArrayBuffer[com.snowplowanalytics.model.SimpleEvent] =  {
    var resultList = scala.collection.mutable.ArrayBuffer.empty[SimpleEvent]
    for (a <- dynamoArray) {
      val result = a.map(unpack)
      println(result)
      resultList += SimpleEvent(Some(result(0).toInt), result(3), result(1), result(0).toInt)
    }
    resultList
  }


  /**
   * Function takes collection of SimpleEvents and returns a JSON DruidResponse
   */
  def countDruidResponse(eventArray: List[com.snowplowanalytics.model.SimpleEvent]):  scala.collection.immutable.Iterable[spray.json.JsObject] = {
    val groupByTimestamp = eventArray.groupBy(_.timestamp)
    val typeAndCountExtracted = groupByTimestamp.mapValues(_.map(x => Map(x.eventType -> x.count.toString)))
    typeAndCountExtracted map {
      keyVal => {
        val k = keyVal._1.toJson
        val v = keyVal._2.toJson
        JsObject("timestamp" -> k, "result" -> v)
      }
    }
  }




  /**
   * Helper Function for custom marshaller for SimpleEvent model
   */
  def serialize(events: List[SimpleEvent]): List[spray.json.JsObject] = {
    for (event <- events)
      yield SimpleEventJsonProtocol.eventFormat.write(event)
  }

  /**
   * Helper Function for convertDataStage - unpacks DynamoDB table values
   */
  def unpack(x: Any): String = x match {
    case Attribute("Count", value) => value.getN
    case Attribute("EventType", value) => value.getS
    case Attribute("Timestamp", value) => value.getS
    case Attribute("CreatedAt", value) => value.getN
  }

  //////////////////////////////////////////////////////////////
  // Testing data and methods below
  //////////////////////////////////////////////////////////////

  def getEvents(): List[SimpleEvent] = {
    testEvents.toList
  }

  def getEventById(eventId: Long): Option[SimpleEvent] = {
    testEvents find (_.id == Some(eventId))
  }

  def addEvent(event: SimpleEvent): SimpleEvent = {
    val maxId = testEvents.map(_.id).flatten.max + 1
    val newEvent = event.copy(id = Some(maxId))
    testEvents += newEvent
    newEvent
  }

  def updateEvent(event: SimpleEvent): Boolean = {
    testEvents.indexWhere(_.id == event.id) match {
      case -1 => false
      case i => testEvents.update(i, event); true
    }
  }

  def getEventsFilterGreen: List[SimpleEvent] = {
    testEvents.toList.filter(x => x.eventType == "Green")
  }

  def deleteEvent(id: Long): Unit = {
    getEventById(id) match {
      case Some(event) => testEvents -= event
      case None =>
    }
  }
}