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

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorLogging
import akka.actor.Props
import spray.routing.HttpService

/**
 * Factory method for Props configuration files for actors
 */
object ApiRouterActor {
  def props(eventRoute: ActorRef): Props = Props(new ApiRouterActor(eventRoute))
}

/**
 * Routes the incoming request.  If the route begins with "api" the request is passed
 * along to the matching spray routing actor (if there's a match)
 *
 * Other routes are assumed to be static resources and are served from the resource
 * directory on the classpath.  getFromResourceDirectory takes the remainder of the path
 * so a route like "index.html" is completed with the classpath resource "dist/index.html"
 * or returns a 404 if it's not found.
 *
 * To run the front end app in dev mode change "dist" to "app"
 */
class ApiRouterActor(eventRoute: ActorRef) extends Actor
  with HttpService
  with ActorLogging {

  def actorRefFactory = context
  def receive = runRoute {
    compressResponseIfRequested() {
      pathPrefix("api") {
        pathPrefix("events") { ctx => eventRoute ! ctx }
      } ~
        getFromResourceDirectory("dist")
    }

  }

}
