package com.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Instant;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ProducerClient {

    private static final Random RANDOM = new Random();

    public static void main(String[] args) {

        // Basic Cluster Configuration
        // ---------------------------------------------------
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092,localhost:9093,localhost:9094"); // Broker list
        props.put("client.id", "transfer-producer-1"); // Logical producer name

        // Serialization Configuration
        // ---------------------------------------------------
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // Safe & Idempotent Delivery
        // ---------------------------------------------------
        props.put("acks", "all"); // Wait for all in-sync replicas + min-insync replicas ( topic / broker config )
        props.put("enable.idempotence", "true"); // Avoid duplicates on retry
        props.put("max.in.flight.requests.per.connection", "5"); // Parallel requests (≤5 for EOS)
        props.put("retries", Integer.MAX_VALUE); // Retry forever
        props.put("retry.backoff.ms", "100"); // Wait before retrying
        props.put("delivery.timeout.ms", "120000"); // Max delivery wait time
        props.put("request.timeout.ms", "30000"); // Broker response timeout

        // Throughput Optimization
        // ---------------------------------------------------
        props.put("batch.size", "16384"); // 16KB batch size
        props.put("linger.ms", "5"); // Wait to group more records
        props.put("compression.type", "none"); // Compression for efficiency
        props.put("sticky.partitioning.linger.ms", "100"); // Use same partition for short bursts

        // Memory & Buffering
        // ---------------------------------------------------
        props.put("buffer.memory", "33554432"); // 32MB buffer size
        props.put("max.block.ms", "60000"); // Block producer on full buffer
        props.put("max.request.size", "1048576"); // Max size per request (1MB)

        // Partitioning Strategy
        // ---------------------------------------------------
        props.put("partitioner.class", "com.example.CustomPartitioner"); // Optional
        // custom routing

        // Interceptor Hook (Optional)
        // ---------------------------------------------------
        // props.put("interceptor.classes", "com.example.Interceptor");

        // (Optional) Transactional Producer
        // props.put("transactional.id", "transfer-tx-001");
        // props.put("transaction.timeout.ms", "60000");

        // (Optional) Security Configuration
        // props.put("security.protocol", "SSL");
        // props.put("ssl.keystore.location", "/path/to/keystore.jks");
        // props.put("ssl.keystore.password", "your-password");

        // Create Kafka Producer
        // ---------------------------------------------------
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        // ransfer Event Generator
        // ---------------------------------------------------
        List<String> transferTypes = List.of("NEFT", "IMPS", "UPI", "RTGS");
        List<String> statusTypes = List.of("PENDING", "SUCCESS", "FAILED");
        String topic = "transfer-events";

        for (int i = 0; i < Integer.MAX_VALUE; i++) {

            String transferType = transferTypes.get(RANDOM.nextInt(transferTypes.size()));
            String status = statusTypes.get(RANDOM.nextInt(statusTypes.size()));

            String transactionId = UUID.randomUUID().toString();
            String fromAccount = "ACC" + (100000 + RANDOM.nextInt(900000));
            String toAccount = "ACC" + (100000 + RANDOM.nextInt(900000));
            double amount = Math.round((100 + RANDOM.nextDouble() * 9900) * 100.0) / 100.0;
            String currency = "INR";
            String timestamp = Instant.now().toString();
            String failureReason = "FAILED".equals(status) ? "Insufficient Balance" : null;

            String value = String.format("""
                    {
                      "transaction_id": "%s",
                      "from_account": "%s",
                      "to_account": "%s",
                      "amount": %.2f,
                      "currency": "%s",
                      "transfer_type": "%s",
                      "timestamp": "%s",
                      "status": "%s"%s
                    }
                    """,
                    transactionId,
                    fromAccount,
                    toAccount,
                    amount,
                    currency,
                    transferType,
                    timestamp,
                    status,
                    failureReason != null ? String.format(",\n  \"failure_reason\": \"%s\"", failureReason) : "");

            // -------------------------- Send Message --------------------------
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, transferType, value);
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    System.err.println("❌ Send failed: " + exception.getMessage());
                } else {
                    System.out.printf("✅ Sent: Key=%s | Partition=%d | Offset=%d%n",
                            transferType, metadata.partition(), metadata.offset());
                }
            });

            try {
                TimeUnit.MILLISECONDS.sleep(1); // Simulate real delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // -------------------------- Cleanup --------------------------
        producer.flush();
        producer.close();
        System.out.println("🎉 Kafka Producer completed.");
    }
}
