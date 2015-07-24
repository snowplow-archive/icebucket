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
import com.snowplowanalytics.services.Aggregation._

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
   * Function gets all events matching range between 2 time buckets in DynamoDB
   * scala.collection.immutable.Iterable[spray.json.JsObject]
   */
  def druidRequest(druidRequest: DruidRequest): String = {
    val intervals = druidRequest.intervals(0).split("/")
    val table: Table = dynamoDB.table(druidRequest.dataSource).get
    if (druidRequest.granularity == "hour") {
      val timestampResult: Seq[awscala.dynamodbv2.Item] = table.scan(Seq("Timestamp" -> cond.between(intervals(0), BucketingStrategyHour.bucket(intervals(1)))))
      val attribsOfElements: Seq[Seq[awscala.dynamodbv2.Attribute]] = timestampResult.map(_.attributes)
      countHourlyDruidResponse(convertDataStageHour(attribsOfElements).toList).toJson.toString
    } else if (druidRequest.granularity == "day"){
      val timestampResult: Seq[awscala.dynamodbv2.Item] = table.scan(Seq("Timestamp" -> cond.between(intervals(0), BucketingStrategyDay.bucket(intervals(1)))))
      val attribsOfElements: Seq[Seq[awscala.dynamodbv2.Attribute]] = timestampResult.map(_.attributes)
      countHourlyDruidResponse(convertDataStageDay(attribsOfElements).toList).toJson.toString
      // parse all timestamps by day - normalize
      // aggregate by day
    } else {
      val timestampResult: Seq[awscala.dynamodbv2.Item] = table.scan(Seq("Timestamp" -> cond.between(intervals(0), intervals(1))))
      val attribsOfElements: Seq[Seq[awscala.dynamodbv2.Attribute]] = timestampResult.map(_.attributes)
      countDruidResponse(convertDataStage(attribsOfElements).toList).toJson.toString
    }
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