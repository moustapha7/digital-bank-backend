package com.sid.digitalbankbackend;

import com.sid.digitalbankbackend.entities.AccountOperation;
import com.sid.digitalbankbackend.entities.CurrentAccount;
import com.sid.digitalbankbackend.entities.Customer;
import com.sid.digitalbankbackend.entities.SavingAccount;
import com.sid.digitalbankbackend.enums.AccountStatus;
import com.sid.digitalbankbackend.enums.OperationType;
import com.sid.digitalbankbackend.repositories.AccountOperationRepository;
import com.sid.digitalbankbackend.repositories.BankAccountRepository;
import com.sid.digitalbankbackend.repositories.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class DigitalBankBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DigitalBankBackendApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(
			CustomerRepository customerRepository,
			BankAccountRepository bankAccountRepository,
			AccountOperationRepository accountOperationRepository
	){
		return args -> {
			Stream.of("Tapha","Mana","Hassan","Imane","Mohamed").forEach(name->{
				Customer customer=new Customer();
				customer.setNom(name);
				customer.setEmail(name+"@gmail.com");
				customerRepository.save(customer);
			});

			customerRepository.findAll().forEach(cust->{

				CurrentAccount currentAccount = new CurrentAccount();
				currentAccount.setId(UUID.randomUUID().toString());
				currentAccount.setBalance(Math.random()*90000);
				currentAccount.setCreatedAt(new Date());
				currentAccount.setStatus(AccountStatus.CREATED);
				currentAccount.setCustomer(cust);
				currentAccount.setOverDraft(900);
				bankAccountRepository.save(currentAccount);

				SavingAccount savingAccount = new SavingAccount();
				savingAccount.setId(UUID.randomUUID().toString());
				savingAccount.setBalance(Math.random()*90000);
				savingAccount.setCreatedAt(new Date());
				savingAccount.setStatus(AccountStatus.CREATED);
				savingAccount.setCustomer(cust);
				savingAccount.setInterestRate(5.5);
				bankAccountRepository.save(savingAccount);

			});

			bankAccountRepository.findAll().forEach(acc->{
				for(int i =0;i<10;i++) {
					AccountOperation accountOperation = new AccountOperation();
					accountOperation.setOperationDate(new Date());
					accountOperation.setAmount(Math.random() * 12000);
					accountOperation.setType(Math.random() > 0.5 ? OperationType.DEBIT : OperationType.CREDIT);
					accountOperation.setBankAccount(acc);
					accountOperationRepository.save(accountOperation);
				}
				});
		};
	}

}
