package com.example;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class ProducerClient {
    public static void main(String[] args) {

        Properties properties = new Properties();

        // client.id is used to identify the producer in Kafka
        properties.put("client.id", "producer-client");

        // get cluster-metadata from the bootstrap servers
        properties.put("bootstrap.servers", "localhost:9092,localhost:9093,localhost:9094");

        // key.serializer is used to serialize the key of the message
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // value.serializer is used to serialize the value of the message
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // batch.size is used to control the number of records sent in a single batch
        properties.put("batch.size", "16384"); // 16 KB
        // linger.ms is used to control the time to wait before sending a batch
        properties.put("linger.ms", "20"); // 5 milliseconds

        // compression.type is used to specify the compression type for the messages
        properties.put("compression.type", "gzip"); // gzip, snappy, lz4

        // acks is used to control the acknowledgment behavior of the producer
        // properties.put("acks", "0"); // 0 means no acknowledgment is required
        // properties.put("acks", "1"); // 1 means the leader will acknowledge the
        // record after it has been written to
        // its local log
        properties.put("acks", "all"); // all means the leader will wait for the full set of in-sync replicas to
                                       // acknowledge the record + min.insync.replicas ( topic/broker config )

        // partitioner.class is used to specify the custom partitioner
        properties.put("partitioner.class", "com.example.CustomPartitioner");

        // retry
        properties.put("retries", Integer.MAX_VALUE); // Number of retries for sending a record in case of failure
        properties.put("retry.backoff.ms", "100"); // Time to wait before retrying a failed send
        // delivery.timeout.ms is used to control the maximum time to wait for a record
        // to be acknowledged
        properties.put("delivery.timeout.ms", "120000"); // 2 minutes

        // buffer.memory is used to control the total memory available to the producer
        // for buffering
        properties.put("buffer.memory", "33554432"); // 32 MB
        // max.block.ms is used to control the maximum time to block when sending a
        // record
        properties.put("max.block.ms", "60000"); // 60 seconds

        // max.in.flight.requests.per.connection is used to control the maximum number
        // of
        // in-flight requests per connection
        properties.put("max.in.flight.requests.per.connection", "5"); // 5 in-flight

        // idempotence
        properties.put("enable.idempotence", "true"); // Enable idempotence to ensure
                                                      // exactly-once delivery semantics

        // request
        properties.put("request.timeout.ms", "30000"); // 30 seconds
        properties.put("max.request.size", "1048576"); // 1 MB

        // Interceptor configuration
        properties.put("interceptor.classes", "com.example.Interceptor");

        // Create a KafkaProducer instance with the specified properties
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        // transfer happens here, such as sending messages
        List<String> transferTypes = List.of("NEFT", "IMPS", "UPI", "RTGS");
        String topic = "transfer-events";

        for (int i = 0; i < Integer.MAX_VALUE; i++) {

            String key = transferTypes.get((int) (Math.random() * transferTypes.size()));
            // transfer event data in json format
            String value = "{\"event\":\"transfer\",\"amount\":1000,\"currency\":\"INR\"}";
            // String value =
            // "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n";
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);

            // Send the record to the Kafka topic
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    System.err.println("Error sending record: " + exception.getMessage());
                } else {
                    System.out.printf("Record sent to topic %s partition %d with offset %d%n",
                            metadata.topic(), metadata.partition(), metadata.offset());
                }
            });

            try {
                TimeUnit.MILLISECONDS.sleep(1); // Simulate processing delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Consumer interrupted.");
            }

        }
        producer.flush(); // Ensure all records are sent before closing the producer
        producer.close(); // Close the producer to release resources
        System.out.println("Producer client finished sending messages.");

    }
}

// Producer

// High-throughput producer
// - partition count
// - batch.size
// - linger.ms
// - compression.type
// - acks
// - max.in.flight.requests.per.connection
// - retries
// - retry.backoff.ms

// safe-producer ( durability )

// - acks
// - retries
// - idempotence
// - delivery.timeout.ms
// - request.timeout.ms

// ----------------------------------------

// if RF = 3, acks = all, min.insync.replicas = 2 , tolerake one broker failure

// ----------------------------------------