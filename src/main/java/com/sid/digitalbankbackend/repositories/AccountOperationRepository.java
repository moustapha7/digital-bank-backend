package com.sid.digitalbankbackend.repositories;

import com.sid.digitalbankbackend.entities.AccountOperation;
import com.sid.digitalbankbackend.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountOperationRepository extends JpaRepository<AccountOperation, Long> {
}
