package com.sid.digitalbankbackend.web;


import com.sid.digitalbankbackend.dtos.CustomerDTO;
import com.sid.digitalbankbackend.entities.Customer;
import com.sid.digitalbankbackend.exceptions.CustomerNotFoundException;
import com.sid.digitalbankbackend.services.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CustomerRestController {

    @Autowired
    private BankAccountService bankAccountService;

    @GetMapping("/customers")
    public List<CustomerDTO> customers()
    {
        return bankAccountService.listCustomers();
    }

    @GetMapping("/customers/{id}")
    public CustomerDTO getCustomer(@PathVariable(name="id") Long customerId) throws CustomerNotFoundException {
        return bankAccountService.getCustomer(customerId);
    }

    @PostMapping("/customers")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO)
    {
        return bankAccountService.saveCustomer(customerDTO);
    }

    @PutMapping("/customers/{customerId}")
    public CustomerDTO updateCustomer(@PathVariable Long customerId,@RequestBody CustomerDTO customerDTO)
    {
        return bankAccountService.updateCustomer(customerDTO);
    }

    @DeleteMapping("/customers/{id}")
    public void deleteCustomer(@PathVariable Long id) throws CustomerNotFoundException {
          bankAccountService.deleteCustomer(id);
    }
}
