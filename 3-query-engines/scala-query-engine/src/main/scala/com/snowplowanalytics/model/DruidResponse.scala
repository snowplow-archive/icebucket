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

case class DruidResponse(timestamp: String, result: List[com.snowplowanalytics.model.AggregationDynamoDB])

/**"
 * Implements spray-json support so DruidResponse case class can be marshalled
 * to/from json when accepting and completing requests
 *
 *
 *   [
 *    {
 *      "timestamp": "2012-01-01T00:00:00.000Z",
 *      "result": { "Green": 23, "Red": 12, "Blue": 14 }
 *    },
 *    {
 *      "timestamp": "2012-01-02T00:00:00.000Z",
 *      "result": { "Purple": 17, "Red": 120, "Yellow": 4 }
 *    }
 *  ]
 *
 *
 */
//object DruidResponseJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
//  implicit val druidResponseFormat = jsonFormat4(DruidRequest)
//}