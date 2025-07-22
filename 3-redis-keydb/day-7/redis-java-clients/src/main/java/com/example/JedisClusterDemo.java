package com.example;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.sql.Time;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class JedisClusterDemo {
    public static void main(String[] args) {
        // Add all KeyDB cluster nodes (host ports mapped to localhost)
        Set<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort("localhost", 7000));

        try (JedisCluster cluster = new JedisCluster(nodes)) {
            // Set and Get demo
            System.out.println("Writing test data to KeyDB cluster...");
            for (int i = 1; i <= Integer.MAX_VALUE; i++) {
                String key = "user:" + i;
                String value = "value" + i;
                cluster.set(key, value);
                // log hash slot of the key
                int slot = redis.clients.jedis.util.JedisClusterCRC16.getSlot(key);
                System.out.println("Key: " + key + ", Value: " + value + ", Slot: " + slot);
                TimeUnit.MILLISECONDS.sleep(1); // Simulate some delay
            }
            // System.out.println("Reading test data from KeyDB cluster:");
            // for (int i = 1; i <= 10; i++) {
            // String key = "user:" + i;
            // String value = cluster.get(key);
            // System.out.println(key + " => " + value);
            // }
            // Test cluster slot distribution (optional)
            System.out.println("Cluster test completed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
