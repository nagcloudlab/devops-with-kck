package com.example;

import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

public class KeyDBClusterExample {

    public static void main(String[] args) throws InterruptedException {

        // Jedis cluster configuration
        HostAndPort node1 = new HostAndPort("localhost", 7000);
        HostAndPort node2 = new HostAndPort("localhost", 7001);

        // Create a JedisCluster instance
        JedisCluster jedisCluster = new JedisCluster(
                Set.of(node1, node2));

        int count = 0;
        while (true) {

            // Increment the count
            count++;

            try {
                // Set a key in the cluster
                jedisCluster.set("key" + count, "value" + count);
                System.out.println(
                        "Set key: key" + count + " with value: value" + count + " in the cluster, node: ");
            } catch (Exception e) {
                System.err.println("Error setting key: " + e.getMessage());
            }

            try {
                // Get the value of the key from the cluster
                String value = jedisCluster.get("key" + count);
                System.out.println("Key: key" + count + ", Value: " + value);
            } catch (Exception e) {
                System.err.println("Error getting key: " + e.getMessage());
            }

            Thread.sleep(1000); // Sleep for 1 second to simulate some delay

        }

        // Check if the key exists in the cluster

        // Close the JedisCluster instance
        // jedisCluster.close(); // Uncomment to close the cluster connection

    }

}
