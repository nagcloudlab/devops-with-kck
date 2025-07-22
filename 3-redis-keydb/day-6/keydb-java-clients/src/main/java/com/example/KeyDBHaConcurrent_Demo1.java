package com.example;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.LocalDateTime; // Add this import
import java.util.*;
import java.util.concurrent.*;

public class KeyDBHaConcurrent_Demo1 {
    static final List<String> NODES = Arrays.asList("localhost:6379");
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
            long counter = 0;
            Random rand = new Random();
            Jedis jedis = null;
            while (true) {
                // String node = NODES.get(rand.nextInt(NODES.size()));
                // JedisPool pool = POOLS.get(node);
                jedis = new Jedis("localhost", 6379); // create new connection to redis
                // try (jedis) {
                try {
                    // jedis.auth(PASSWORD);
                    // jedis.waitReplicas(2, 5000);
                    jedis.set("mykey-" + counter, "value-" + counter);
                    System.out.printf("[%s] [WRITE] mykey = value-%d to %s%n",
                            LocalDateTime.now(), counter, "localhost:6379");
                    counter++;
                } catch (Exception e) {
                    System.out.printf("[%s] [WRITE FAIL] %s: %s%n",
                            LocalDateTime.now(), "localhost:6379", e.getMessage());
                } finally {
                    if (jedis != null) {
                        jedis.close();
                    }
                }
            }
        });

    }

}
