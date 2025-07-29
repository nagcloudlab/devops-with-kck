package com.example;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.KafkaConsumer;

public class ConsumerClient {
    public static void main(String[] args) {

        Properties properties = new Properties();
        // client-id
        properties.put("client.id", "consumer-client");
        // consumer group-id ( notification )
        properties.put("group.id", "g1");
        // bootstrap servers
        properties.put("bootstrap.servers", "localhost:9092,localhost:9093,localhost:9094");
        // key deserializer
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        // value deserializer
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        // max poll records
        properties.put("max.poll.records", "5000");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down consumer...");
            consumer.close(); // Leave group request to Group Coordinator
            System.out.println("Consumer closed.");
        }));

        // Subscribe to topics (e.g., "transfer-events")
        consumer.subscribe(java.util.Collections.singletonList("transfer-events"));

        // Poll for new messages
        while (true) {
            System.out.println("Polling for new messages...");
            var records = consumer.poll(java.time.Duration.ofMillis(100)); // Fetch Request
            if (records.count() > 0) {
                System.out.println("Received " + records.count() + " new messages.");
            } else {
                System.out.println("No new messages found.");
            }

            // Process records
            records.forEach(record -> {
                System.out.printf("Received message with key: %s, value: %s, partition: %d, offset: %d%n",
                        record.key(), record.value(), record.partition(),
                        record.offset());

                try {
                    TimeUnit.MILLISECONDS.sleep(3); // Simulate processing delay
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Consumer interrupted.");
                }
            });

            // Commit offsets if necessary
            consumer.commitSync();
        }

    }
}