Steps:

1. Write a single-threaded Scala app which reads a set of example events from a TSV file. Lets use UUIDs to test uniqueness
2. The Scala app should generate a serialized HyperLogLog for each period covered in the TSV file and write the HLL to DynamoDB
3. Write a single-threaded Scala app which for a given timebucket (or buckets), reads all the serialized HLLs from DynamoDB
4. This Scala app should merge them into a final HLL per period then return the uniqueness count per period
5. Let's check the HLL estimates versus the exact uniqueness counts from the original TSV file

