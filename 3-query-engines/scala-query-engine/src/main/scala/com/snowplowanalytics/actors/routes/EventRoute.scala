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
package com.snowplowanalytics.actors.routes

import com.snowplowanalytics.model.{DruidRequest, AggregationDynamoDB, DataSchema, Body, QueryGranularity, MetricUnit, ParserTypes, TimestampSpec, ParseSpec}
import com.snowplowanalytics.services.EventService
import com.snowplowanalytics.services.SchemaService
import akka.actor.Props
import spray.http.StatusCodes
import spray.httpx.SprayJsonSupport
import spray.routing.HttpService
import akka.actor.Actor
import org.slf4j.LoggerFactory
import spray.httpx.SprayJsonSupport._
import spray.json._
import DefaultJsonProtocol._

/**
* Factory method for Props configuration files for actors
*/
object EventRoute {
  def props: Props = Props(new EventRoute())
}

/**
 * Actor that handles requests that begin with "events"
 */
class EventRoute() extends Actor with EventRouteTrait {
  def actorRefFactory = context
  def receive = runRoute(eventRoute)
}


/**
 * Separate routing logic in an HttpService trait so that the
 * routing logic can be tested outside of an actor system in specs/mockito tests
 */
trait EventRouteTrait extends HttpService with SprayJsonSupport {

  import com.snowplowanalytics.model.AggregationDynamoDBJsonProtocol
  import com.snowplowanalytics.services.RequestJsonProtocol._

  private val eventService = EventService
  private val schemaService = SchemaService
  val log = LoggerFactory.getLogger(classOf[EventRouteTrait])

  def postDruidRequest = post {
    path ("druid"){
      entity(as[DruidRequest]) { druidEvent =>
        log.debug(s"Druid Event ${druidEvent}")
        val result = eventService.druidRequest(druidEvent)
        complete(result)
      }
    }
  }

  def postSchemaRequest = post {
    path ("schema"){
      entity(as[DataSchema]) { schemaEvent =>
        log.debug(s"Schema Event ${schemaEvent}")
        val result = schemaService.schemaRequest(schemaEvent)
        complete(result)
      }
    }
  }

  // main function that handles routes to the EventService
  val eventRoute = {
    get {
      pathEnd {
        complete {
          implicit val eventFormat = jsonFormat4(AggregationDynamoDB)
          eventService.getEvents
        }
      }
    } ~ {
    postDruidRequest
    } ~ {
    postSchemaRequest  
    }
  }

}
