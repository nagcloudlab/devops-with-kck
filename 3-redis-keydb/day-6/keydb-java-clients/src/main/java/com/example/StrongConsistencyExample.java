package com.example;

import java.util.concurrent.ExecutorService;

import redis.clients.jedis.Jedis;

public class StrongConsistencyExample {

    public static void main(String[] args) {

        ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(2);

        // Writer Thread
        executor.submit(() -> {
            int counter = 0;
            while (true) {
                try {
                    Jedis masterJedis = new Jedis("localhost", 6379);
                    // masterJedis.waitReplicas(1, 5000); // with caution
                    masterJedis.set("mykey", "value-" + counter);
                    System.out.printf("[%s] [WRITE] mykey = value-%d to master%n",
                            java.time.LocalDateTime.now(), counter);
                    counter++;
                } catch (Exception e) {
                    System.out.printf("[%s] [WRITE FAIL] master: %s%n",
                            java.time.LocalDateTime.now(), e.getMessage());
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        // Reader Thread
        executor.submit(() -> {
            while (true) {
                Jedis replicaJedis = new Jedis("localhost", 6379);
                try {
                    String value = replicaJedis.get("mykey");
                    System.out.printf("[%s] [READ] mykey = %s from replica%n",
                            java.time.LocalDateTime.now(), value);
                } catch (Exception e) {
                    System.out.printf("[%s] [READ FAIL] replica: %s%n",
                            java.time.LocalDateTime.now(), e.getMessage());
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

            }
        });

    }

}
