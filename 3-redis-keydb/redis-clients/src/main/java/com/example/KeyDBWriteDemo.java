package com.example;

import redis.clients.jedis.Jedis;

public class KeyDBWriteDemo {
    public static void main(String[] args) throws InterruptedException {
        // Use the host/port of any KeyDB master node
        String[] masterNodes = {
                "localhost", // node 1
                "localhost", // node 2 (use real IPs)
                "localhost" // node 3
        };
        int[] ports = { 6379, 6380, 6381 };

        int counter = 1;
        int current = 0;
        while (true) {
            String host = masterNodes[current];
            int port = ports[current];
            try (Jedis jedis = new Jedis(host, port)) {
                String key = "key" + counter;
                String value = "value" + counter;
                jedis.set(key, value);
                System.out.println("📝 Written to " + host + ":" + port + ": " + key + " = " + value);

                // Read-back (eventual consistency!)
                String read = jedis.get(key);
                System.out.println("🔁 Read-back: " + key + " = " + read);

                counter++;
                // Next master (round-robin)
                current = (current + 1) % masterNodes.length;
                Thread.sleep(1000);
            } catch (Exception e) {
                System.err.println("❌ Write failed to " + host + ":" + port + ": " + e.getMessage());
                Thread.sleep(2000);
            }
        }
    }
}
