package com.example.transfer_service;

import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;

public interface TransactionRepository extends CassandraRepository<Transaction, UUID> {
    // Additional query methods can be defined here if needed

}
