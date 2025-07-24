package com.example.transfer_service;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
    // Additional query methods can be defined here if needed

}
