package com.example;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.LocalDateTime; // Add this import
import java.util.*;
import java.util.concurrent.*;

public class KeyDBHaConcurrent_Demo1 {
    static final List<String> NODES = Arrays.asList("localhost:6379", "localhost:6380", "localhost:6381");
    static final String PASSWORD = "mypass";
    static final Map<String, JedisPool> POOLS = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(8);

        for (String node : NODES) {
            String[] parts = node.split(":");
            POOLS.put(node, new JedisPool(config, parts[0], Integer.parseInt(parts[1])));
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down Jedis pools...");
            POOLS.values().forEach(JedisPool::close);
        }));

        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Writer Thread
        executor.submit(() -> {
            int counter = 0;
            Random rand = new Random();
            while (true) {
                String node = NODES.get(rand.nextInt(NODES.size()));
                JedisPool pool = POOLS.get(node);
                try (Jedis jedis = pool.getResource()) {
                    // jedis.auth(PASSWORD);
                    // jedis.waitReplicas(2, 5000);
                    jedis.set("mykey", "value-" + counter);
                    System.out.printf("[%s] [WRITE] mykey = value-%d to %s%n",
                            LocalDateTime.now(), counter, node);
                    counter++;
                } catch (Exception e) {
                    System.out.printf("[%s] [WRITE FAIL] %s: %s%n",
                            LocalDateTime.now(), node, e.getMessage());
                }
                Thread.sleep(1000);
            }
        });

        // Reader Thread
        executor.submit(() -> {
            while (true) {
                for (String node : NODES) {
                    JedisPool pool = POOLS.get(node);
                    try (Jedis jedis = pool.getResource()) {
                        // jedis.auth(PASSWORD);
                        String value = jedis.get("mykey");
                        System.out.printf("[%s] [READ]  mykey from %s: %s%n",
                                LocalDateTime.now(), node, value);
                    } catch (Exception e) {
                        System.out.printf("[%s] [READ FAIL] %s: %s%n",
                                LocalDateTime.now(), node, e.getMessage());
                    }
                }
                Thread.sleep(1000);
            }
        });
    }
}
