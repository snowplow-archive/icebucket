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
package com.snowplowanalytics.model

import spray.json._
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

case class AggregationDynamoDB(id: Option[Int], timestamp: String, eventType: String, count: Int)

/**
 * Implements spray-json support so AggregationDynamoDB case class can be marshalled
 * to/from json when accepting and completing requests. By having this
 * marshaller in scope an HttpService can automatically handle things
 * like List[AggregationDynamoDB] or Option[AggregationDynamoDB]
 */
object AggregationDynamoDBJsonProtocol extends DefaultJsonProtocol {

  //implicit val eventFormat = jsonFormat4(AggregationDynamoDB)
  implicit object eventFormat extends RootJsonFormat[AggregationDynamoDB] {
    def write(c: AggregationDynamoDB) = JsObject(
      "timestamp" -> JsString(c.timestamp),
      "eventType" -> JsString(c.eventType),
      "count" -> JsNumber(c.count)
    )

    def read(value: JsValue) = {
      value.asJsObject.getFields("timestamp", "eventType", "count") match {
        case Seq(JsString(timestamp), JsString(eventType), JsNumber(count)) =>
          AggregationDynamoDB(Some(123), timestamp, eventType, count.toInt)
        case _ => throw new DeserializationException("AggregationDynamoDB expected")
      }
    }
  }
}
