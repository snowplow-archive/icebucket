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
import com.snowplowanalytics.model.{DataSchema, Body, QueryGranularity, MetricUnit, ParserTypes, TimestampSpec, ParseSpec}
import com.snowplowanalytics.services.ZipUtils.jarSlicer

/**
 * SchemaService Object holds all the functions for DynamoDB access to the DataSchema table
 */
object SchemaService {

  // sets DynamoDB client to us-east-1
  implicit val dynamoDB = DynamoDB.at(Region.US_EAST_1)

  // sets dynamodb table name
  val tablename = "data-schema"

  // sets up table and dynamodb connection
  val table: Table = dynamoDB.table(tablename).get

  // sets various settings for global secondary index
  val dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  val timezone = TimeZone.getTimeZone("UTC")
  val Hash = aws.model.KeyType.HASH
  val Range = aws.model.KeyType.RANGE
  val Include = aws.model.ProjectionType.INCLUDE
  val All = aws.model.ProjectionType.ALL

  /**
   * Function gets all events matching range between 2 time buckets in DynamoDB
   * scala.collection.immutable.Iterable[spray.json.JsObject]
   */
  def schemaRequest(q: DataSchema): String = {
    //val result: Seq[Item] = table.scan(Seq("dataSource" -> cond.gt("wikipedia")))
    //val attribsOfElements: Seq[Seq[awscala.dynamodbv2.Attribute]] = result.map(_.attributes)
    //convertDataStage(attribsOfElements).toString
    jarSlicer("in.zip", "out.zip", "def.txt")
    "new jar created"
  }

  /**
   * Helper Function for converting DynamoDB to AggregationDynamoDB model
   */
  def convertDataStage(dynamoArray: Seq[Seq[awscala.dynamodbv2.Attribute]]) {
    var resultList = scala.collection.mutable.ArrayBuffer.empty[String]
    for (a <- dynamoArray) {
      val result = a.map(unpack)
      //println(result.toString)
      resultList += result(3)
    }
    resultList
  }


  /**
   * Helper Function for convertDataStage - unpacks DynamoDB table values
   */
  def unpack(x: Any): String = x match {
    case Attribute("dataSource", value) => value.getS
    case Attribute("metricSpec", value) => value.getS
    case Attribute("Body", value) => value.getS
    case Attribute("queryGranularity", value) => value.getS
  }

}