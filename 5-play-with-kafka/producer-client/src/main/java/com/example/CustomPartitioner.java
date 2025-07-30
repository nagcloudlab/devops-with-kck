package com.example;

public class CustomPartitioner implements org.apache.kafka.clients.producer.Partitioner {

    @Override
    public void configure(java.util.Map<String, ?> configs) {
        // Configuration can be done here if needed
    }

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes,
            org.apache.kafka.common.Cluster cluster) {
        // Custom partitioning logic based on the key
        String keyString = (String) key;
        switch (keyString) {
            case "NEFT":
                return 0; // Partition 0 for NEFT
            case "IMPS":
                return 0; // Partition 1 for IMPS
            case "UPI":
                return 1; // Partition 2 for UPI
            case "RTGS":
                return 2; // Partition 3 for RTGS
            default:
                // Default partitioning logic, e.g., round-robin
                return Math.abs(keyString.hashCode()) % cluster.availablePartitionsForTopic(topic).size();
        }
    }

    @Override
    public void close() {
        // Cleanup resources if needed
    }

}
