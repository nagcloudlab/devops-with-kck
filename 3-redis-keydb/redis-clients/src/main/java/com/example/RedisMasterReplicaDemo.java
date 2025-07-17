package com.example;

import redis.clients.jedis.Jedis;
import java.time.LocalDateTime;

public class RedisMasterReplicaDemo {
    public static void main(String[] args) throws InterruptedException {

        // Connect to master and replica
        Jedis master = new Jedis("localhost", 6379);
        Jedis replica = new Jedis("localhost", 6380);

        System.out.println("Connected to master and replica...");

        int i = 1;

        while (true) {
            String key = "counter";
            String value = "val-" + i + " @ " + LocalDateTime.now();

            // WRITE to master
            master.set(key, value);
            System.out.println("WROTE to master: " + value);

            // Wait a bit for replication to occur
            Thread.sleep(100);

            // READ from replica
            String replicaValue = replica.get(key);
            System.out.println("READ from replica: " + replicaValue);

            i++;
            Thread.sleep(1000); // 1 second interval
        }
    }
}