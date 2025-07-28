package com.example.transfer_service;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
public class CassandraConfig {

    @Bean
    public CqlSession cassandraSession() {
        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress("127.0.0.1", 9042))
                // .addContactPoint(new InetSocketAddress("127.0.0.2", 9042))
                // .addContactPoint(new InetSocketAddress("127.0.0.3", 9042))
                .withLocalDatacenter("datacenter1")
                .withKeyspace("txns_ks")
                .build();
    }
}
