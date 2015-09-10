# Ice Bucket

UNRELEASED. An opinionated framework for analytics-on-write on event streams using key-value storage



## Introduction

Important Top Level Files
```
icebucket
├── 0-common
│   └── integration-tests
│       └── data_generator.py
├── 1-agg-engines
│   └── lambda-agg-engine
│       └── AWS Lambda in Scala for Aggregations
│       └── See [README](1-agg-engines/lambda-agg-engine/README.md)
├── 3-query-engines
│   └── data-schema-engine
│       └── AWS Lambda in Scala for DATASCHEMA submission
│       └── See [README](3-query-engines/data-schema-engine-engine/README.md)
├── 3-query-engines
│   └── scala-query-engine
│       └── REST API in Scala Spary for DRUID request and for DATASCHEMA submission 
│       └── See [README](3-query-engines/scala-query-engine/README.md)
└── 4-visualization
    └── HTTP/REST Single Page Application in Angular.js and Bootstrap for basic table listing of DynamoDB table data
        └── See [README](4-visulatization/README.md)
```
