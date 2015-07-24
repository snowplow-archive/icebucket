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

// Scala
import awscala.dynamodbv2._
import spray.json._
import spray.json.DefaultJsonProtocol._

// package import
import com.snowplowanalytics.model.{DruidResponse, SimpleEventJsonProtocol, SimpleEvent, DruidRequest}


/**
 * Aggregation Object holds all the functions counting items
 */
object Aggregation {

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
   * Helper Function for converting DynamoDB to SimpleEvent model
   */
  def convertDataStageHour(dynamoArray: Seq[Seq[awscala.dynamodbv2.Attribute]]): scala.collection.mutable.ArrayBuffer[com.snowplowanalytics.model.SimpleEvent] =  {
    var resultList = scala.collection.mutable.ArrayBuffer.empty[SimpleEvent]
    for (a <- dynamoArray) {
      val result = a.map(unpackHour)
      println(result)
      resultList += SimpleEvent(Some(result(0).toInt), result(3), result(1), result(0).toInt)
    }
    resultList
  }

  /**
   * Helper Function for converting DynamoDB to SimpleEvent model
   */
  def convertDataStageDay(dynamoArray: Seq[Seq[awscala.dynamodbv2.Attribute]]): scala.collection.mutable.ArrayBuffer[com.snowplowanalytics.model.SimpleEvent] =  {
    var resultList = scala.collection.mutable.ArrayBuffer.empty[SimpleEvent]
    for (a <- dynamoArray) {
      val result = a.map(unpackDay)
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
    val typeAndCountExtracted = groupByTimestamp.mapValues(_.map(x => Map(x.eventType -> x.count)))
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

  def unpackHour(x: Any): String = x match {
    case Attribute("Count", value) => value.getN
    case Attribute("EventType", value) => value.getS
    case Attribute("Timestamp", value) => BucketingStrategyHour.downsample(value.getS)
    case Attribute("CreatedAt", value) => value.getN
  }

  def unpackDay(x: Any): String = x match {
    case Attribute("Count", value) => value.getN
    case Attribute("EventType", value) => value.getS
    case Attribute("Timestamp", value) => BucketingStrategyDay.downsample(value.getS)
    case Attribute("CreatedAt", value) => value.getN
  }

}