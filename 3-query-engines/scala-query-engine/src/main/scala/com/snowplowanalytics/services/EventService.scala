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

//import com.amazonaws.services.dynamodbv2.model.{ScanRequest, ProjectionType, KeyType}
//import com.sun.xml.internal.bind.v2.schemagen.xmlschema.AttributeType

// Scala
//import com.amazonaws.services.dynamodbv2._
import awscala._
import awscala.dynamodbv2._
import spray.json._
import com.amazonaws.services.{ dynamodbv2 => aws }

// package import
import com.snowplowanalytics.model.SimpleEvent


// AWS Authentication
// http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/auth/DefaultAWSCredentialsProviderChain.html
import com.amazonaws.auth.profile.ProfileCredentialsProvider

// AWS DynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
//import com.amazonaws.services.dynamodbv2.document.{Table, AttributeUpdate, DynamoDB, Item}


import awscala._
import awscala.dynamodbv2._
import awscala.dynamodbv2.GlobalSecondaryIndex
import com.amazonaws.services.{ dynamodbv2 => aws }
import spray.json._


// Scala

/**
 * Object sets up singleton that finds AWS credentials for DynamoDB to access the
 * aggregation records table.
 */
object EventService {

  val dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  val timezone = TimeZone.getTimeZone("UTC")

  implicit val dynamoDB = DynamoDB.at(Region.US_EAST_1)

  val Hash = aws.model.KeyType.HASH
  val Range = aws.model.KeyType.RANGE
  val Include = aws.model.ProjectionType.INCLUDE
  val All = aws.model.ProjectionType.ALL
  val tablename = "my-table"
  val eventType = "Red"
  val timestamp = "2015-06-05T12:56:00.000"
  val table: Table = dynamoDB.table(tablename).get

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


  //////////////////////////////////////////////////////////////
  // temp data
  //////////////////////////////////////////////////////////////

  import EventData.testEvents

  def getEvents2: List[SimpleEvent] = {
    testEvents.toList
  }

  def getEventById(eventId: Long): Option[SimpleEvent] = {
    testEvents find (_.id == Some(eventId))
  }

  def getEvents3: List[SimpleEvent] = {
    testEvents.toList.filter(x => x.eventType == "Green")
  }





  // provide "Red", "2015-06-20T12:32:00.00" and get count back
  def getEvents5(): List[SimpleEvent] = {

      println(getCountByEventTypeTimestamp(eventType, timestamp, table))
    List(SimpleEvent(Some(123), Some("asdfasdf"), Some("asdfasdf"), Some(123)))
  }

  def getEvents(): List[SimpleEvent] = {

    val bucket = "2015-06-05T12:56:00.000"
    val timestampResult: Seq[awscala.dynamodbv2.Item] = table.scan(Seq("Timestamp" -> cond.eq(bucket)))
    val attribsOfFourElements: Seq[Seq[awscala.dynamodbv2.Attribute]] = timestampResult.map(_.attributes)


    convertDataStage1(attribsOfFourElements)


    List(SimpleEvent(Some(123), Some("asdfasdf"), Some("asdfasdf"), Some(123)))
  }

  // provide "Red", "2015-06-20T12:32:00.00" and get count back
  // getCountByEventTypeTimestamp(eventType, timestamp, table)
  def getCountByEventTypeTimestamp(eventType: String, timestamp: String, table: Table): Seq[String] = {
    val findRedwithTimestamp: Seq[Item] = table.queryWithIndex(
      index = globalSecondaryIndex,
      keyConditions = Seq("EventType" -> cond.eq(eventType), "Timestamp" -> cond.eq(timestamp))
    )
    findRedwithTimestamp.flatMap(_.attributes.find(_.name == "Count").map(_.value.n.get))
  }

  // val bucket = "2015-06-05T12:56:00.000"
  // getListOfEventsByTimestamp(bucket, table)
  // res2: Seq[String] = ArrayBuffer(Green, Blue, Red, Yellow)
  def getListOfEventsByTimestamp(bucket: String, table: Table): Seq[String] = {
    val timestampResult: Seq[awscala.dynamodbv2.Item] = table.scan(Seq("Timestamp" -> cond.eq(bucket)))
    val attribsOfFourElements: Seq[Seq[awscala.dynamodbv2.Attribute]] = timestampResult.map(_.attributes)
    timestampResult.flatMap(_.attributes.find(_.name == "EventType").map(_.value.s.get))
  }

  // convertDataStage1(attribsOfFourElements)
  def convertDataStage1(myArray: Seq[Seq[awscala.dynamodbv2.Attribute]]) {
     for (a <- myArray) {
       val result = a.map(unpack)
       //createSimpleEvents(result)
     }
  }


  //def createSimpleEvents(x:Any): SimpleEvent = {
    //SimpleEvent(Some(123), Some(x(1).toString), Some(x(0).toString), Some(123))
  //}



  // attribsOfFourElements.map(_.foreach(unpack))
  def unpack(x: Any): String = x match {
    case Attribute("Count", value) => value.getN
    case Attribute("EventType", value) => value.getS
    case Attribute("CreatedAt", value) => value.getN
    case Attribute("Timestamp", value) => value.getS
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

  def deleteEvent(id: Long): Unit = {
    getEventById(id) match {
      case Some(event) => testEvents -= event
      case None =>
    }
  }
}
