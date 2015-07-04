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
package com.snowplowanalytics.config

import akka.actor.ActorSystem
import com.snowplowanalytics.actors.routes.ApiRouterActor
import com.snowplowanalytics.actors.routes.EventRoute

/**
 * Factory method for ActorSystemBean class
 */
object ActorSystemBean {
  def apply(): ActorSystemBean = new ActorSystemBean()
}

/**
 * Defines an actor system with the actors used by
 * the spray-events application
 */
class ActorSystemBean {
  import com.snowplowanalytics.actors.routes.EventRoute._
  import com.snowplowanalytics.actors.routes.ApiRouterActor._

  implicit val system = ActorSystem("event")

  lazy val eventRoute = system.actorOf(EventRoute.props, "event-route")
  lazy val apiRouterActor = system.actorOf(ApiRouterActor.props(eventRoute), "api-router")

}
