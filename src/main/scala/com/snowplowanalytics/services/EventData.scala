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

import com.snowplowanalytics.model.SimpleEvent
import scala.collection.mutable.ArrayBuffer

/**
 * SimpleEvent data stored in a mutable array for demonstration purposes.
 * This would normally be replaced by a DAO layer that makes calls to
 * a database or external service that persists person data.
 *
 *  TODO: Connect DynamoDB Utils here from Spark Streaming Project
 *
 */
object EventData {
  val testEvents = ArrayBuffer(
    SimpleEvent(Some(1), "2015-06-30T12:00:00.000", "Green", 55),
    SimpleEvent(Some(2), "2015-06-30T12:00:00.000", "Yellow", 100),
    SimpleEvent(Some(3), "2015-06-30T12:00:00.000", "Blue", 300),
    SimpleEvent(Some(4), "2015-06-30T12:00:00.000", "Red", 900),
    SimpleEvent(Some(5), "2015-06-30T11:00:00.000", "Green", 215),
    SimpleEvent(Some(6), "2015-06-30T11:00:00.000", "Yellow", 110),
    SimpleEvent(Some(7), "2015-06-30T11:00:00.000", "Blue", 130),
    SimpleEvent(Some(8), "2015-06-30T11:00:00.000", "Red", 190),
    SimpleEvent(Some(9), "2015-06-30T10:00:00.000", "Green", 25),
    SimpleEvent(Some(10), "2015-06-30T10:00:00.000", "Yellow", 210),
    SimpleEvent(Some(11), "2015-06-30T10:00:00.000", "Blue", 230),
    SimpleEvent(Some(12), "2015-06-30T10:00:00.000", "Red", 290)
  )
}
