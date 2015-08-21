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
import java.text.SimpleDateFormat

/**
 * Object uses downsampling method to create metadata from each
 * EventType log record. Parsing the ISO 8601
 * datetime stamp to the HOUR means downsampling aka reducing
 * precision.
 *
 * Bucketing
 * A family of aggregations that build buckets, where each bucket
 * is associated with a key and an EventType criterion. When the
 * aggregation is executed, all the buckets criteria are evaluated
 * on every EventType in the context and when a criterion matches,
 * the EventType is considered to "fall in" the relevant bucket.
 * By the end of the aggregation process, we’ll end up with a
 * list of buckets - each one with a set of EventTypes that
 * "belong" to it.
 *
 */
object BucketingStrategyHour {

  private val BucketToHourFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:59:59.000")
  private val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
  private val AggregateHourFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:00:00.000")

  /**
   * Function formats date string to bucket query plan
   * based on our bucketing strategy of 1 hour buckets.
   * Bucketing means downsampling aka reducing
   * precision.
   */
  def bucket(dateString: String): String = {
    val parsed = format.parse(dateString)
    BucketToHourFormatter.format(parsed)
  }

  /**
   * Function formats DynamoDB date string to aggregration buckets
   * based on our bucketing strategy of 1 hour buckets.
   */
  def downsample(dateString: String): String = {
    val parsed = format.parse(dateString)
    AggregateHourFormatter.format(parsed)
  }
}