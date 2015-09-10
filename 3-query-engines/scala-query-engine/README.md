# Spray server that has couple routes:

1. First route DRUID json request gets aggregate data from DynanoDB
2. Second route SCHEMA json request checks AWS Lambda in place and creates if none found

## Files 

```
└── src
    └── main
        ├── resources
        │   ├── application.conf
        │   └── logback.xml
        └── scala
            └── com
                └── snowplowanalytics
                    ├── actors
                    │   └── routes
                    │       ├── ApiRouterActor.scala
                    │       └── EventRoute.scala
                    ├── config
                    │   ├── ActorServiceSystem.scala
                    │   └── Boot.scala
                    ├── model
                    │   ├── AggregationDynamoDB.scala
                    │   ├── Body.scala
                    │   ├── DataSchema.scala
                    │   ├── DruidRequest.scala
                    │   ├── DruidResponse.scala
                    │   ├── MetricUnit.scala
                    │   ├── ParseSpec.scala
                    │   ├── ParserTypes.scala
                    │   ├── QueryGranularity.scala
                    │   └── TimestampSpec.scala
                    └── services
                        ├── Aggregration.scala
                        ├── BucketDay.scala
                        ├── BucketHour.scala
                        ├── DynamoDBUtils.scala
                        ├── EventData.scala
                        ├── EventService.scala
                        ├── RequestJsonProtocol.scala
                        ├── SchemaService.scala
                        └── ZipUtils.scala
```

## Run project
`sbt run`


## Number 1 - DRUID json request
SCREENSHOT POST
https://bigsnarf.files.wordpress.com/2015/09/screen-shot-2015-09-10-at-9-05-31-am.png

URL
`http://localhost:8080/api/events/druid`


Body of request
```
{
    "queryType": "timeseries", 
    "dataSource":"my-table11",
    "granularity": "day",
    "intervals": [
        "2015-06-04T12:54:00.000/2015-07-28T23:56:00.000"
        ]
}
```

## Number 2 - SCHEMA json request
SCREENSHOT POST
https://bigsnarf.files.wordpress.com/2015/09/screen-shot-2015-09-10-at-9-06-14-am.png

URL
`http://localhost:8080/api/events/schema`


Body of request
```
{
    "dataSchema": {
        "dataSource": "wikipedia",
        "parser": {
            "type": "string",
            "parseSpec": {
                "format": "json",
                "timestampSpec": {
                    "column": "timestamp",
                    "format": "auto"
                }
            }
        },
        "metricsSpec": [
            {
                "type": "count1",
                "name": "count2"
            },
            {
                "type": "doubleSum1",
                "name": "added1",
                "fieldName": "added2"
            },
            {
                "type": "doubleSum2",
                "name": "deleted1",
                "fieldName": "deleted2"
            },
            {
                "type": "doubleSum3",
                "name": "delta1",
                "fieldName": "delta2"
            }
        ],
        "granularitySpec": {
            "queryGranularity": "NONE"
        }
    }
}
```