package com.sid.digitalbankbackend.repositories;

import com.sid.digitalbankbackend.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
