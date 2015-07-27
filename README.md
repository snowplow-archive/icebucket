# Ice Bucket

UNRELEASED. An opinionated framework for analytics-on-write on event streams using key-value storage



## Introduction
This is an example [Spray] REST API application with an Angular JS front end.  The REST API accepts a DRUID request and returns a DRUID response for events that were 

processed from our [Kinesis] [aws-kinesis] stream of events ([introductory blog post] [blog-post]). When you make a DRUID request

to the REST API it retrieves the previous counted `minute` aggregates from [DynamoDB] [aws-dynamodb].

This was built by the Data Science team at [Snowplow Analytics] [snowplow], who use AWS DynamoDB and Scala Spray in their projects.

**Running this requires an Amazon AWS account, and will incur charges.**

_See also:_ [Spark Streaming Example Project][spark-streaming-example-project] | [Spark Example Project] [spark-example-project]

## Developer Quickstart

Start the application with `sbt run`.

Navigate to the home page [http://localhost:8080/index.html](http://localhost:8080/index.html) to see the Angular app

Or do a GET request to [http://localhost:8080/api/events](http://localhost:8080/api/events) to hit the REST endpoint directly

## Front End Development

The `\dist` directory has been included in the project and is already built including minification.
	Running the server will serve the app from here so that it runs out of the box.  Any changes you make in here will get clobbered next time
 you build the front end with grunt.  Normally this folder is not included in source control.

For front end development make your changes to the `\app` folder and follow the steps below:

## Front End Setup

Front end requires Bower, which requires npm

### Install NPM:
But this is an scala app and I already have SBT!!  For the front end dependency management you will need npm.

Options
- https://www.npmjs.org/doc/README.html
- `brew install npm`

### Install bower
Bower is a front end dependency manager `npm install -g bower` (Linux users might have to run with sudo)

Download all the front end dependencies with `bower install`

*They will be in your `app\bower_components` directory as instructed by `.bowerrc`*

### Install Grunt CLI
Grunt is a front end task runner
`npm install -g grunt-cli`

Wire up the dependencies with `grunt bowerInstall`

*This modifies `app\index.html` to include your dependencies*


### Change where static content is served from

Lastly in `/src/main/scala/com/example/actors/routes/ApiRouterActor.scala` change where static content is served from:

		getFromResourceDirectory("dist")

		//becomes

		getFromResourceDirectory("app")

