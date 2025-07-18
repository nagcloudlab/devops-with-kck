package com.example;

import redis.clients.jedis.Jedis;
import java.time.LocalDateTime;

public class RedisMasterReplicaDemo {
    public static void main(String[] args) throws InterruptedException {

        // Connect to master and replica
        Jedis master1 = new Jedis("localhost", 6380);
        Jedis replica1 = new Jedis("localhost", 6381);

        System.out.println("Connected to master and replica...");

        int i = 1;

        while (true) {
            // Generate a key-value pair
            String key = "counter-" + i;
            String value = "val-" + i + " @ " + LocalDateTime.now();

            try {
                // WRITE to master
                master1.set(key, value);
                System.out.println("WROTE to master1: " + value);
            } catch (Exception e) {
                // Attempt to write to the second master if the first fails
                System.out.println("Failed to write to master1" + e.getMessage());
            }

            // Wait a bit for replication to occur
            Thread.sleep(100);

            // READ from replica
            try {
                String replicaValue = replica1.get(key);
                System.out.println("READ from replica1: " + replicaValue);
            } catch (Exception e) {
                System.out.println("Failed to read from replica1: " + e.getMessage());
            }
            i++;
            Thread.sleep(100); // Sleep to simulate time between writes
        }
    }
}