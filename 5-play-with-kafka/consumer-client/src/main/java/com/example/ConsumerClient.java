package com.example;

import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class ConsumerClient {

    public static void main(String[] args) {

        // Basic Consumer Identification
        // ----------------------------------
        Properties props = new Properties();
        props.put("client.id", "consumer-client-1"); // Logical consumer ID
        props.put("group.id", "g1"); // Consumer group ID

        // Cluster Connectivity
        // ----------------------------------
        props.put("bootstrap.servers", "localhost:9092,localhost:9093,localhost:9094");

        // Deserialization (Key & Value)
        // ----------------------------------
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        // Rebalance Strategy
        // ----------------------------------
        props.put("partition.assignment.strategy", CooperativeStickyAssignor.class.getName());

        // Poll Behavior & Fetch Tuning
        // ----------------------------------
        props.put("fetch.min.bytes", "1"); // Minimum data to fetch
        props.put("fetch.max.bytes", "5242880"); // Max data to fetch (50 MB)
        props.put("max.partition.fetch.bytes", "1048576"); // Max per-partition

        props.put("max.poll.records", "500"); // Max records per poll

        // Heartbeats & Session
        // ----------------------------------
        props.put("heartbeat.interval.ms", "3000"); // Heartbeat interval
        props.put("session.timeout.ms", "45000"); // Max allowed delay in heartbeats

        // Application Processing Limits
        // ----------------------------------
        props.put("max.poll.interval.ms", "300000"); // Max processing time between polls

        // Offset Reset Behavior
        // ----------------------------------
        props.put("auto.offset.reset", "earliest"); // Start from earliest if no
        // offset

        // Manual Offset Commit
        // ----------------------------------
        props.put("enable.auto.commit", "false");
        props.put("auto.commit.interval.ms", "5000"); // Commit offsets every 5 seconds

        // Rack Awareness (Optional)
        // --------------------------
        props.put("client.rack", "hyd-rack1");

        // Optional Logging / Metrics
        // --------------------------
        // props.put("metrics.recording.level", "DEBUG");
        // props.put("metric.reporters", "org.apache.kafka.common.metrics.JmxReporter");

        // -------------------------- Create Kafka Consumer --------------------------
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        // -------------------------- Shutdown Hook --------------------------
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("🛑 Shutdown initiated...");
            consumer.close(); // Leaves group, commits offsets if needed
            System.out.println("✅ Consumer closed gracefully.");
        }));

        // -------------------------- Topic Subscription --------------------------
        consumer.subscribe(Collections.singletonList("transfer-events"));

        // -------------------------- Polling Loop --------------------------
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("📥 Received | Key: %s | Partition: %d | Offset: %d%n%s%n\n",
                        record.key(), record.partition(), record.offset(), record.value());

                try {
                    TimeUnit.MILLISECONDS.sleep(3); // Simulate processing
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            // -------------------------- Manual Offset Commit --------------------------
            if (!records.isEmpty()) {
                consumer.commitSync();
                System.out.println("✅ Offsets committed");
            }
        }
    }
}
