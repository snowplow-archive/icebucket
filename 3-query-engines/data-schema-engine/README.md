# This is a AWS Kinesis to AWS Lambda:

Simple AWS Kinesis Post and save to DynamoDB. Almost a Kinesis to DynamoDB writer for AWS Lambda

## Files 

```
├── project
│   └── plugins.sbt
└── src
    └── main
        └── scala
            └── com
                └── snowplowanalytics
                    ├── DynamoDBUtility.scala
                    ├── Main.scala
                    └── json.json
```

## Assemble project into fat jar
`sbt assembly`


## DRUID json request to AWS Kinesis
Post to Kinesis stream to activate the AWS Lambda


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

# Getting your code to AWS Lambda Service


### Uploader for S3Lamba
```
aws lambda create-function \
--region us-west-2 \
--function-name lambda-function-in-scala \
--zip-file fileb://aws-lambda-scala/S3Lambda/target/scala-2.11/lambda-demo-assembly-1.0.jar \
--role arn:aws:iam::account-id:role/lambda_basic_execution  \
--handler com.snowplowanalytics::Main \
--runtime java8 \
--timeout 15 \
--memory-size 512
```


### Uploader for Kinesis
```
aws lambda create-function \
--region us-west-2 \
--function-name lambda-function-in-scala-kinesis \
--zip-file fileb://aws-lambda-scala/Kinesis/target/scala-2.11/lambda-in-scala-assembly-1.0.jar \
--role arn:aws:iam::account-id:role/lambda_basic_execution \
--handler com.snowplowanalytics.ProcessKinesisEvents::recordHandler \
--runtime java8 \
--timeout 15 \
--memory-size 512
```

### Update Kinesis Function
```
aws lambda update-function-code \
--region us-west-2 \
--function-name lambda-function-in-scala-kinesis \
--zip-file fileb://aws-lambda-scala/Kinesis/target/scala-2.11/lambda-in-scala-assembly-1.0.jar
```