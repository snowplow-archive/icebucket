package com.snowplowanalytics

import java.io.IOException
import com.amazonaws.services.lambda.runtime.events.KinesisEvent
import com.amazonaws.services.lambda.runtime.events.KinesisEvent.KinesisEventRecord
import scala.collection.JavaConversions._

// Parser
case class TimestampSpec(column: String, format: String)
case class ParseSpec(format: String, timestampSpec: TimestampSpec)
case class ParserTypes(`type`: String, parseSpec: ParseSpec)
case class Parser(parser: ParserTypes)

// MetricsSpec
case class MetricUnit(`type`: String, name: String, fieldName: Option[String] = None)

// GranularitySpec
case class QueryGranularity(queryGranularity: String)

// Body of Request 
case class Body(dataSource:String, parser:ParserTypes, metricsSpec: List[MetricUnit], granularitySpec: QueryGranularity)

// DataSchema
case class DataSchema(dataSchema:Body)

class ProcessKinesisEvents {
	    
    import com.fasterxml.jackson.databind.ObjectMapper
    import com.fasterxml.jackson.module.scala.DefaultScalaModule

    val scalaMapper = {  
        new ObjectMapper().registerModule(new DefaultScalaModule)
    }

    def recordHandler(event: KinesisEvent) {
        for (rec <- event.getRecords) {
            val record = new String(rec.getKinesis.getData.array())
            val dataSchema = scalaMapper.readValue(record, classOf[DataSchema])
	        println(dataSchema)
	    }
	}
}

