package com.example.transfer_service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Data;

@Data
@Table("transactions")
public class Transaction {

    @PrimaryKey
    private UUID id = UUID.randomUUID(); // Unique identifier for the transaction
    @Column("account_number")
    private String accountNumber;
    private BigDecimal amount;
    @Column("transaction_type")
    private String transactionType; // e.g., "DEPOSIT", "WITHDRAWAL"

}
