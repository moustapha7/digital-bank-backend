package com.sid.digitalbankbackend.services;


import com.sid.digitalbankbackend.dtos.*;
import com.sid.digitalbankbackend.entities.*;
import com.sid.digitalbankbackend.enums.OperationType;
import com.sid.digitalbankbackend.exceptions.BalanceNotSufficientException;
import com.sid.digitalbankbackend.exceptions.BankAccountNotFoundException;
import com.sid.digitalbankbackend.exceptions.CustomerNotFoundException;
import com.sid.digitalbankbackend.mappers.BankAccountMapperImpl;
import com.sid.digitalbankbackend.repositories.AccountOperationRepository;
import com.sid.digitalbankbackend.repositories.BankAccountRepository;
import com.sid.digitalbankbackend.repositories.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Transactional
public class BankAccountServiceImpl implements BankAccountService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private AccountOperationRepository accountOperationRepository;

    Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private BankAccountMapperImpl dtoMapper;

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer= customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer= customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public void deleteCustomer(Long customerId)
    {
        customerRepository.deleteById(customerId);
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {

        Customer customer = customerRepository.findById(customerId).orElse(null);
        if(customer==null)
            throw new CustomerNotFoundException("Customer not found");

        CurrentAccount currentAccount= new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreatedAt(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setOverDraft(overDraft);
        currentAccount.setCustomer(customer);
        CurrentAccount savedBankAccount = bankAccountRepository.save(currentAccount);
        return dtoMapper.fromCurrentBankAccount(savedBankAccount);
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestedRate, Long customerId) throws CustomerNotFoundException {

        Customer customer = customerRepository.findById(customerId).orElse(null);
        if(customer==null)
            throw new CustomerNotFoundException("Customer not found");

        SavingAccount savingAccount= new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreatedAt(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setInterestRate(interestedRate);
        savingAccount.setCustomer(customer);
        SavingAccount savedBankAccount = bankAccountRepository.save(savingAccount);
        return dtoMapper.fromSavingBankAccount(savedBankAccount);
    }


    @Override
    public List<CustomerDTO> listCustomers() {
        List<Customer> customers = customerRepository.findAll();
         List<CustomerDTO> customerDTOS = customers.stream().map(customer -> dtoMapper.fromCustomer(customer))
                        .collect(Collectors.toList());

       /* List<CustomerDTO> customerDTOS = new ArrayList<>();
        for(Customer customer:customers) {
           CustomerDTO customerDTO= dtoMapper.fromCustomer(customer);
           customerDTOS.add(customerDTO);
        }*/
        return customerDTOS;
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(()-> new BankAccountNotFoundException("BankAccount not found"));

        if(bankAccount instanceof  SavingAccount){
            SavingAccount savingAccount =(SavingAccount) bankAccount;
            return dtoMapper.fromSavingBankAccount(savingAccount);

        }
        else{
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return dtoMapper.fromCurrentBankAccount(currentAccount);
        }

    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {

       // BankAccount bankAccount = getBankAccount(accountId);
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(()-> new BankAccountNotFoundException("BankAccount not found"));
        if(bankAccount.getBalance() < amount)
            throw new BalanceNotSufficientException("Balance not sufficient ou Solde Insufisant");

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        //modification du solde
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {


        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(()-> new BankAccountNotFoundException("BankAccount not found"));
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        //modification du solde
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);

    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {

        debit(accountIdSource,amount,"Transfert to "+accountIdDestination);
        credit(accountIdDestination,amount,"Transfert from "+accountIdSource);
    }

    @Override
    public  List<BankAccountDTO> bankAccountList()
    {
        List<BankAccount>  bankAccounts = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccount -> {
            if(bankAccount instanceof SavingAccount){
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return  dtoMapper.fromSavingBankAccount(savingAccount);
            }
            else{
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return  dtoMapper.fromCurrentBankAccount(currentAccount);
            }
        }).collect(Collectors.toList());
        return bankAccountDTOS;
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
       Customer customer =  customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
       return dtoMapper.fromCustomer(customer);
    }


    @Override
    public  List<AccountOperationDTO> accountHistory(String accountId){
        List<AccountOperation> accountOperations=  accountOperationRepository.findByBankAccountId(accountId);
        return accountOperations.stream().map(op ->dtoMapper.fromAccountOperation(op))
                .collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);

        if(bankAccount == null)
            throw new BankAccountNotFoundException("Account not found");
         Page<AccountOperation> accountOperations =accountOperationRepository.findByBankAccountId(accountId, PageRequest.of(page,size));
         AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
         List<AccountOperationDTO> accountOperationDTOS = accountOperations.getContent().stream().map(op -> dtoMapper.fromAccountOperation(op))
                 .collect(Collectors.toList());
         accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOS);
         accountHistoryDTO.setAccountId(bankAccount.getId());
         accountHistoryDTO.setBalance(bankAccount.getBalance());
         accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());

        return accountHistoryDTO;
    }
}
