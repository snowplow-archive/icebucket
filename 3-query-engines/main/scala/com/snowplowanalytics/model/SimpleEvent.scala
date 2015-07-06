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

import spray.json.DefaultJsonProtocol

case class SimpleEvent(id: Option[Long], timestamp: String, eventType: String, count: Int)

/**
 * Implements spray-json support so SimpleEvent case class can be marshalled
 * to/from json when accepting and completing requests.  By having this
 * marshaller in scope an HttpService can automatically handle things
 * like List[SimpleEvent] or Option[SimpleEvent]
 */
object SimpleEventJsonProtocol extends DefaultJsonProtocol {
  implicit val eventFormat = jsonFormat4(SimpleEvent)
}
