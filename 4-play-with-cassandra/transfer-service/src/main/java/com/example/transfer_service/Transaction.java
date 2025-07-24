package com.example.transfer_service;

import java.util.UUID;

import lombok.Data;

@Data
public class Transaction {

    private UUID id;
    private String accountNumber;
    private double amount;
    private String transactionType; // e.g., "DEPOSIT", "WITHDRAWAL"

}
