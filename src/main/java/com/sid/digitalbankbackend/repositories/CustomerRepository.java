package com.sid.digitalbankbackend.repositories;

import com.sid.digitalbankbackend.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByNomContains(String keyword);

    @Query("select c from Customer c where c.nom like :kw")
    List<Customer> searchCustomer(@Param("kw") String keyword);


}
