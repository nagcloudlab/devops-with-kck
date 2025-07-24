package com.example.transfer_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UPITransferService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, isolation = org.springframework.transaction.annotation.Isolation.READ_COMMITTED)
    public String initiateTransfer(String fromAccountNumber, String toAccountNumber, double amount) {

        Account fromAccount = accountRepository.findById(fromAccountNumber)
                .orElseThrow(() -> new RuntimeException("From account not found"));
        Account toAccount = accountRepository.findById(toAccountNumber)
                .orElseThrow(() -> new RuntimeException("To account not found"));

        if (fromAccount.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance in from account");
        }

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction debTransaction = new Transaction();
        debTransaction.setAccountNumber(fromAccountNumber);
        debTransaction.setAmount(-amount);
        debTransaction.setTransactionType("DEBIT");
        transactionRepository.save(debTransaction);

        Transaction credTransaction = new Transaction();
        credTransaction.setAccountNumber(toAccountNumber);
        credTransaction.setAmount(amount);
        credTransaction.setTransactionType("CREDIT");
        transactionRepository.save(credTransaction);

        return "Transfer successful from " + fromAccountNumber + " to " + toAccountNumber;

    }

}
