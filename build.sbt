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

scalaVersion  := "2.10.4"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/"
)

libraryDependencies ++= {
  val awsSdk = "1.9.34"
  val akkaV  = "2.3.3"
  val sprayV = "1.3.0"
  Seq(
    "com.amazonaws"       %   "aws-java-sdk"      % awsSdk,
    "io.spray"            %   "spray-servlet"     % sprayV,
    "io.spray"            %   "spray-routing"     % sprayV,
    "io.spray"            %   "spray-testkit"     % sprayV,
    "io.spray"            %   "spray-client"      % sprayV,
    "io.spray"            %   "spray-util"        % sprayV,
    "io.spray"            %   "spray-caching"     % sprayV,
    "io.spray"            %   "spray-can"         % sprayV,
    "io.spray"            %%  "spray-json"        % "1.3.2",
    "com.typesafe.akka"   %%  "akka-slf4j"        % "2.3.11",
    "ch.qos.logback"      %   "logback-classic"   % "1.0.13",
    "com.typesafe.akka"   %%  "akka-actor"        % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"      % akkaV,
    "org.specs2"          %%  "specs2"            % "2.2.3"    % "test"
  )
}

Revolver.settings

unmanagedResourceDirectories in Compile <+= (baseDirectory)

excludeFilter in unmanagedResources := HiddenFileFilter || "node_modules*" || "project*" || "target*"
