package com.example;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

public class KeyDBPoolMultiMasterDemo {
    public static void main(String[] args) throws InterruptedException {
        // List your KeyDB master nodes
        String[] masterHosts = { "localhost", "localhost", "localhost" }; // change to your real hostnames
        int[] masterPorts = { 6391, 6392, 6393 };

        // 1. Create a JedisPool for each master
        List<JedisPool> pools = new ArrayList<>();
        for (int i = 0; i < masterHosts.length; i++) {
            pools.add(new JedisPool(new JedisPoolConfig(), masterHosts[i], masterPorts[i]));
        }

        int counter = 1;
        int current = 0;
        while (true) {
            JedisPool pool = pools.get(current); // RR
            String host = masterHosts[current];
            int port = masterPorts[current];

            try (Jedis jedis = pool.getResource()) {
                String key = "key" + counter;
                String value = "value" + counter;

                jedis.set(key, value);
                System.out.println("📝 Written to " + host + ":" + port + ": " + key + " = " + value);

                String read = jedis.get(key);
                System.out.println("🔁 Read-back: " + key + " = " + read);

                counter++;
                // Move to next master (round-robin)
                current = (current + 1) % pools.size();
                // Thread.sleep(1000);
            } catch (Exception e) {
                System.err.println("❌ Write failed to " + host + ":" + port + ": " + e.getMessage());
                // Skip this node for a while, continue with next node
                current = (current + 1) % pools.size();
                Thread.sleep(2000);
            }
        }
    }
}
