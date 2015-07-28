#!/usr/bin/env python

import datetime, json, uuid, time
from boto import kinesis
from functools import partial
from random import choice
import boto.dynamodb2
from boto.dynamodb2.fields import HashKey, RangeKey, KeysOnlyIndex, GlobalAllIndex
from boto.dynamodb2.table import Table
from boto.dynamodb2.types import NUMBER
from boto.dynamodb2.types import STRING
from random import randint


"""
This program loads synthetic aggregated EventTypes log data simulating logs with datetime stamp and EventType
"""

# Selection of EventType values
colors = ['Red','Orange','Yellow','Green','Blue']

# AWS DynamoDB settings
table_name = "my-table"
aws_region_name = "us-east-1"

# AWS DynamoDB generator
def picker(seq):
  """
  Returns a new function that can be called without arguments 
  to select and return a random color
  """
  return partial(choice,seq)

def get_event_id():
  """
  Returns UUID as string
  """
  return str(uuid.uuid4())

def get_event_time():
  """
  Returns datetime stamp in ISO format
  """
  return datetime.datetime.now().isoformat()

def create_event():
  """
  Returns a choice of color and builds and event
  """
  event_id = get_event_id()
  color_choice = picker(colors)
  return (event_id, {
    "dateString": get_event_time(),
    "eventType": color_choice(),
    "count": get_count()
  })

def write_event(conn, stream_name):
  """
  Returns the event and event event_payload
  """
  event_id, event_payload = create_event()
  event_json = json.dumps(event_payload)
  conn.put_record(stream_name, event_json, event_id)
  print(event_json, event_id)
  return event_id

def create_table():
  """
  Creates DynamoDB table called my-table with Global
  Secondary Index
  """
  return Table.create('my-table', schema=[
    HashKey('CreatedAt'),
    RangeKey('Count'),
  ], throughput={
    'read': 10,
    'write': 10,
  }, global_indexes=[
    GlobalAllIndex('CountsIndex', parts=[
      HashKey('EventType',data_type=STRING),
      RangeKey('Timestamp',data_type=STRING)
    ],
    throughput={
      'read': 10,
      'write': 10,
    })
  ],
  connection=boto.dynamodb2.connect_to_region('us-east-1')
  )


def get_table():
  return Table('my-table', schema=[
    HashKey('CreatedAt'),
    RangeKey('Count'),
  ], global_indexes=[
     GlobalAllIndex('CountsIndex', parts=[
      HashKey('EventType',data_type=STRING),
      RangeKey('Timestamp',data_type=STRING)
  ])
  ])



def put_table():
event_id = get_event_id()
color_choice = picker(colors)
conn.put_item(data={
    'CreatedAt': randint(1,100000),
    'Count': randint(1,100),
    'EventType': color_choice(),
    'Timestamp':  datetime.datetime.now().isoformat()
})


if __name__ == '__main__':
  conn = create_table()
  while True:
    event_id = put_table(conn)
    print "Event sent to DynamoDB: {}".format(event_id)
    time.sleep(1)