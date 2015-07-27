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

## Copyright and license

Ice Bucket Project is copyright 2015 Snowplow Analytics Ltd.

Licensed under the **[Apache License, Version 2.0] [license]** (the "License");
you may not use this software except in compliance with the License.

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

[travis]: https://travis-ci.org/snowplow/aws-lambda-nodejs-example-project
[travis-image]: https://travis-ci.org/snowplow/aws-lambda-nodejs-example-project.png?branch=master
[license-image]: http://img.shields.io/badge/license-Apache--2-blue.svg?style=flat
[license]: http://www.apache.org/licenses/LICENSE-2.0
[release-image]: http://img.shields.io/badge/release-0.1.0-blue.svg?style=flat
[releases]: https://github.com/snowplow/aws-lambda-nodejs-example-project/releases
[grunt-image]: https://cdn.gruntjs.com/builtwith.png

[spark-example-project]: https://github.com/snowplow/spark-example-project
[spark-streaming-example-project]: https://github.com/snowplow/spark-streaming-example-project

[vagrant-install]: http://docs.vagrantup.com/v2/installation/index.html
[virtualbox-install]: https://www.virtualbox.org/wiki/Downloads

[blog-post]: http://snowplowanalytics.com/blog/2015/07/11/aws-lambda-nodejs-example-project-0.1.0-released/
[020-milestone]: https://github.com/snowplow/aws-lambda-nodejs-example-project/milestones/Version%200.2.0
[dynamodb-table-image]: /docs/dynamodb-table-image.png?raw=true

[aws-lambda]: http://aws.amazon.com/lambda/
[aws-kinesis]: http://aws.amazon.com/kinesis/
[aws-dynamodb]: http://aws.amazon.com/dynamodb
[vagrant-install]: http://docs.vagrantup.com/v2/installation/index.html
[virtualbox-install]: https://www.virtualbox.org/wiki/Downloads
[tim-b]: https://github.com/Tim-B
[tim-b-post]: http://hipsterdevblog.com/blog/2014/12/07/writing-functions-for-aws-lambda-using-npm-and-grunt/
[amazon-kinesis-aggregators]: https://github.com/awslabs/amazon-kinesis-aggregators

[snowplow]: http://snowplowanalytics.com
[icebucket]: https://github.com/snowplow/icebucket