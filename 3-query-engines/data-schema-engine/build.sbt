javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

lazy val root = (project in file(".")).
  settings(
    name := "lambda-in-scala-kinesis",
    version := "1.0",
    scalaVersion := "2.11.4",
    retrieveManaged := true,
    libraryDependencies += "com.amazonaws" % "aws-lambda-java-core" % "1.0.0",
    libraryDependencies += "com.amazonaws" % "aws-lambda-java-events" % "1.0.0",
    libraryDependencies += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.5.2",
    libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.2.11",
    libraryDependencies += "com.amazonaws" % "amazon-kinesis-connectors"  % "1.2.0" % "provided",
    libraryDependencies += "com.twitter"   % "algebird-core_2.10"         % "0.10.2",
    libraryDependencies += "com.amazonaws" % "aws-java-sdk"               % "1.9.34" % "provided",
    libraryDependencies += "com.amazonaws" % "aws-java-sdk-core"          % "1.9.34" % "provided"
  )

mergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
}

