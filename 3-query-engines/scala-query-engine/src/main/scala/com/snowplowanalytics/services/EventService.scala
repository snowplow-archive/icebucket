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

import com.amazonaws.services.dynamodbv2.model.{ProjectionType, KeyType}
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.AttributeType

// Scala
import com.amazonaws.services.dynamodbv2._
import awscala._
import dynamodb2._
import spray.json._
import spray.json.JsonParser
import DefaultJsonProtocol._

// package import
import com.snowplowanalytics.model.SimpleEvent


// AWS Authentication
// http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/auth/DefaultAWSCredentialsProviderChain.html
import com.amazonaws.auth.profile.ProfileCredentialsProvider

// AWS DynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.document.{Table, AttributeUpdate, DynamoDB, Item}




// Scala

/**
 * Object sets up singleton that finds AWS credentials for DynamoDB to access the
 * aggregation records table.
 */
object EventService {

  val dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  val timezone = TimeZone.getTimeZone("UTC")


  /**
   * Function timezone helper
   */
  def timeNow(): String = {
    dateFormatter.setTimeZone(timezone)
    dateFormatter.format(new Date())
  }


  /**
   * Function wraps DynamoDB cred setup
   */
  def setupDynamoClientConnection(awsProfile: String): DynamoDB = {
    val credentials = new ProfileCredentialsProvider(awsProfile)
    val dynamoDB = new DynamoDB(new AmazonDynamoDBClient(credentials))
    dynamoDB
  }


  /**
   * Function wraps get or create item in DynamoDB table
   */
  def setOrUpdateCount(dynamoDB: DynamoDB, tableName: String, timestamp: String, eventType: String, createdAt: String,  updatedAt: String, count: Int){

    val recordInTable = getItem(dynamoDB: DynamoDB, tableName, timestamp, eventType)
    println(recordInTable)
    if (recordInTable == null) {
      putItem(dynamoDB: DynamoDB, tableName, timestamp, eventType, createdAt, updatedAt, count)
    } else {
      val oldCreatedAt = recordInTable.getJSON("CreatedAt").replace("\"", "").replace("\\", "")
      val oldCount = recordInTable.getJSON("Count").toInt
      val newCount = oldCount + count.toInt
      putItem(dynamoDB: DynamoDB, tableName, timestamp, eventType, oldCreatedAt, updatedAt, newCount)
    }
  }
  

  /**
   * Function wraps AWS Java getItemOutcome operation to DynamoDB table
   */
  def getItem(dynamoDB: DynamoDB, tableName: String, timestamp: String, eventType: String): Item = {

    val table = dynamoDB.getTable(tableName)
    val items = table.getItemOutcome("Timestamp", timestamp, "EventType", eventType)
    items.getItem
  }


  /**
   * Function wraps AWS Java putItem operation to DynamoDB table
   */
  def putItem(dynamoDB: DynamoDB, tableName: String, timestamp: String, eventType: String, createdAt: String,  updatedAt: String, count: Int) {

    // AggregateRecords column names
    val tablePrimaryKeyName = "Timestamp"
    val tableEventTypeSecondaryKeyName = "EventType"
    val tableCreatedAtColumnName = "CreatedAt"
    val tableUpdatedAtColumnName = "UpdatedAt"
    val tableCountColumnName = "Count"

    try {
      val time = new Date().getTime - (1 * 24 * 60 * 60 * 1000)
      val date = new Date()
      date.setTime(time)
      dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"))
      val table = dynamoDB.getTable(tableName)
      println("Adding data to " + tableName)

      val item = new Item().withPrimaryKey(tablePrimaryKeyName, timestamp)
        .withString(tableEventTypeSecondaryKeyName, eventType)
        .withString(tableCreatedAtColumnName, createdAt)
        .withString(tableUpdatedAtColumnName, updatedAt)
        .withInt(tableCountColumnName, count)

      // saving the data to DynamoDB AggregrateRecords table
      // println(item)
      table.putItem(item)
    } catch {
      case e: Exception => {
        System.err.println("Failed to create item in " + tableName)
        System.err.println(e.getMessage)
      }
    }
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


  def getEvents4: List[SimpleEvent] = {
    val credentials = new ProfileCredentialsProvider("default")
    val dynamoDB = new DynamoDB(new AmazonDynamoDBClient(credentials))
    val table = dynamoDB.getTable("my-table")
    val items = table.getItemOutcome("Timestamp", "2015-06-30T12:00:00.000", "EventType", "Blue")
    val records = items.getItem.asMap()
    // { Item: {Count=63, EventType=Blue, Timestamp=2015-06-30T12:00:00.000, CreatedAt=2015-06-30T12:00:00.000} }
    //val result = data.convertTo[SimpleEvent]
    List(SimpleEvent(Some(123), records.get("Timestamp").toString, records.get("EventType").toString, records.get("Count").toString.toInt))
  }







  def getEvents: List[SimpleEvent] = {
    //val credentials = new ProfileCredentialsProvider("default")
    //val dynamoDB = new DynamoDB(new AmazonDynamoDBClient(credentials))
    implicit val dynamoDB = DynamoDB.at(Region.US_EAST_1)
    val table: Table = dynamoDB.table("my-table").get
    List(SimpleEvent(Some(123), "asdfas", "asdf", 123))
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
