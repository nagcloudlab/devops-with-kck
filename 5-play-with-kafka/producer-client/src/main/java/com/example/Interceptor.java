package com.example;

import java.util.Map;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class Interceptor implements org.apache.kafka.clients.producer.ProducerInterceptor<String, String> {

    @Override
    public void configure(Map<String, ?> configs) {
    }

    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        // Modify the record before sending, e.g., add a header
        record.headers().add("npci-dc", "hyd".getBytes());
        record.headers().add("npci-tenant", "default".getBytes());
        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
    }

    @Override
    public void close() {
    }

}
