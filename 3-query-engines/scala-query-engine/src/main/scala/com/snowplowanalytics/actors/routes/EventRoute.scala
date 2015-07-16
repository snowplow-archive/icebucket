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

import com.snowplowanalytics.model.{DruidRequest, SimpleEvent}
import com.snowplowanalytics.model.SimpleEventJsonProtocol._
import com.snowplowanalytics.services.EventService
import akka.actor.Props
import spray.http.StatusCodes
import spray.httpx.SprayJsonSupport._
import spray.routing.HttpService
import spray.httpx.SprayJsonSupport
import akka.actor.Actor
import org.slf4j.LoggerFactory

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
trait EventRouteTrait extends HttpService with SprayJsonSupport{

  private val eventService = EventService
  val log = LoggerFactory.getLogger(classOf[EventRouteTrait])

  lazy val singleBucket =  path(Segment) { timestamp =>
    log.debug(s"Get bucket by timestamp: ${timestamp}")
    val bucketedEvent = eventService.getEventsByBucket(timestamp)
    complete(bucketedEvent)
  }

  lazy val timeBuckets = path(Segment / Segment) { (beginTimestamp, endTimestamp) =>
    log.debug(s"Get bucket begin: ${beginTimestamp},  Get bucket end: ${endTimestamp}")
    val bucketedEvents = eventService.getEventsByBucketsBetweenTimestamps(beginTimestamp, endTimestamp)
    complete(bucketedEvents)
  }

  lazy val postDruidRequest = (post & pathEnd) {
    entity(as[SimpleEvent]) { event =>
      log.debug("Druid Event")
      val newEvent = eventService.druidRequest(event)
      complete(StatusCodes.Created, newEvent)
    }
  }

  lazy val getAllTestData = pathEnd {
    complete {
      log.debug("Get All Events")
      val events = eventService.getEvents
      events match {
        case head :: tail => events
        case Nil => StatusCodes.NoContent
      }
    }
  }

  lazy val getTestDataByEventID = path(LongNumber) { eventId =>
    log.debug(s"Get Event by Id:${eventId}")
    val event = eventService.getEventById(eventId)
    complete(event)
  }

  lazy val createEvent = (post & pathEnd) {
    entity(as[SimpleEvent]) { event =>
      log.debug("Create an Event")
      val newEvent = eventService.addEvent(event)
      complete(StatusCodes.Created, newEvent)
    }
  }

  lazy val updateEvent = (put & path(LongNumber) & pathEnd) { eventId =>
    entity(as[SimpleEvent]) { event =>
      log.debug(s"Update an Event with the id: ${eventId}")
      val updatedEvent = eventService.updateEvent(event.copy(id = Some(eventId)))
      updatedEvent match {
        case true => complete(StatusCodes.NoContent)
        case false => complete(StatusCodes.NotFound)
      }
    }
  }

  lazy val deleteEvent = (delete & path(LongNumber) & pathEnd) { eventId =>
    log.debug(s"Delete an Event with the id: ${eventId}")
    eventService.deleteEvent(eventId)
    complete(StatusCodes.NoContent)
  }


  // main function that handles routes to the EventService
  val eventRoute = {
    get {
      singleBucket ~
      timeBuckets ~
      getAllTestData ~
      getTestDataByEventID
    } ~
    postDruidRequest ~
    updateEvent ~
    deleteEvent
  }

}
