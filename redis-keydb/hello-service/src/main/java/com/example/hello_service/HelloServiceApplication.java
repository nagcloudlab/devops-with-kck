package com.example.hello_service;

import java.util.Set;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@SpringBootApplication
@RestController
public class HelloServiceApplication {

	long reqCount = 0; // data-structure to hold the count of calls
	JedisPool pool;

	@PostConstruct
	public void init() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxIdle(20);
		config.setMaxTotal(20);
		config.setMinIdle(20);
		config.setTestOnBorrow(false);
		config.setTestOnReturn(false);
		config.setLifo(false);
		String host = "localhost";
		// Read/write timeout in ms
		int timeout = 2000;
		int port = 6379;
		pool = new JedisPool(config, host, port, timeout);
		Jedis jedis = null;
		boolean broken = false;
		try {
			jedis = pool.getResource();
		} catch (Exception e) {
			broken = true;
		} finally {
			if (broken) {
				pool.returnBrokenResource(jedis);
			} else if (jedis != null) {
				pool.returnResource(jedis);
			}
		}
	}

	@GetMapping("/hello")
	public String sayHello() {
		// Increment the request count
		reqCount++;
		System.out.println("Request count: " + reqCount);
		Jedis jedis = pool.getResource();
		String helloCountStr = jedis.get("helloCount");
		long helloCount = (helloCountStr != null) ? Long.parseLong(helloCountStr) : 0;
		helloCount++;
		jedis.set("helloCount", String.valueOf(helloCount)); // update the count
		jedis.close(); // return the resource to the pool
		return "Hello, World! This endpoint has been called " + helloCount + " times.";
	}

	public static void main(String[] args) {
		SpringApplication.run(HelloServiceApplication.class, args);
	}

}
