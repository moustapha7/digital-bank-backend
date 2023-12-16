package com.sid.digitalbankbackend.repositories;

import com.sid.digitalbankbackend.entities.BankAccount;
import com.sid.digitalbankbackend.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
}
