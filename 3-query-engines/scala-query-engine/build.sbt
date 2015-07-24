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
name := "spray-events"

organization  := "com.snowplowanalytics"

version       := "1.0"

scalaVersion  := "2.10.5"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/"
)

libraryDependencies ++= {
  val awsSdk = "1.9.34"
  Seq(
    "com.amazonaws"       %   "aws-java-sdk"       % awsSdk,
    "org.clapper"         %%  "argot"              % "1.0.3",
    "com.github.seratch"  %%  "awscala"            % "0.5.+",
    "com.twitter"         %   "algebird-core_2.10" % "0.10.2",
    "io.spray"            %%  "spray-can"          % "1.3.3",
    "io.spray"            %%  "spray-routing"      % "1.3.3",
    "io.spray"            %%  "spray-json"         % "1.3.2",
    "com.typesafe.akka"   %%  "akka-actor"         % "2.3.11",
    "com.typesafe.akka"   %%  "akka-slf4j"         % "2.3.11",
    "ch.qos.logback"      %   "logback-classic"    % "1.0.13"
  )
}

Revolver.settings

unmanagedResourceDirectories in Compile <+= (baseDirectory)

excludeFilter in unmanagedResources := HiddenFileFilter || "node_modules*" || "project*" || "target*"
