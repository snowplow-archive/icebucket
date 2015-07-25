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

import akka.io.IO
import spray.can.Http
import ActorServiceSystem._

// Argot
import org.clapper.argot._
import org.clapper.argot.ArgotConverters._

 /**
 * Gets an actor system from the ActorServiceSystem and initializes
 * a stand alone spray-can http server with it.
 */
object Boot extends App {

  // configuration parse
  val parser = new ArgotParser(
   programName = "generated.ProjectSettings.name",
   compactUsage = true,
   preUsage = Some("%s: Version %s. Copyright (c) 2015, %s.".format(
     "Project Icebucket",
     "1.0",
     "Snowplow Analytics")
   )
  )

  val portArgument = parser.option[Int](List("port"), "n", "TCP port to run Web UI (default: 8080)")
  val interfaceArgument = parser.option[String](List("interface"), "interface", "Interface to bind Web UI (default: 0.0.0.0)")

  parser.parse(args)

  val port: Int = portArgument.value.getOrElse(8080)
  val interface: String = interfaceArgument.value.getOrElse("0.0.0.0")


  // main function starting Spray Application and Actor service
  val services = ActorServiceSystem()
  implicit val system = services.system
  val service = services.apiRouterActor

  IO(Http) ! Http.Bind(service, interface = interface, port = port)

}
