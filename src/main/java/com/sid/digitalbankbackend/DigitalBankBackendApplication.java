package com.sid.digitalbankbackend;

import com.sid.digitalbankbackend.dtos.BankAccountDTO;
import com.sid.digitalbankbackend.dtos.CurrentBankAccountDTO;
import com.sid.digitalbankbackend.dtos.CustomerDTO;
import com.sid.digitalbankbackend.dtos.SavingBankAccountDTO;
import com.sid.digitalbankbackend.exceptions.BalanceNotSufficientException;
import com.sid.digitalbankbackend.exceptions.BankAccountNotFoundException;
import com.sid.digitalbankbackend.exceptions.CustomerNotFoundException;
import com.sid.digitalbankbackend.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
public class DigitalBankBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DigitalBankBackendApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(
			BankAccountService bankAccountService
	){
		return args -> {
			Stream.of("Tapha","Mana","Hassan","Imane","Mohamed").forEach(name->{
				CustomerDTO customer=new CustomerDTO();
				customer.setNom(name);
				customer.setEmail(name+"@gmail.com");
				bankAccountService.saveCustomer(customer);
			});

			bankAccountService.listCustomers().forEach(customer -> {
				try {
					bankAccountService.saveCurrentBankAccount(Math.random()*9000, 900, customer.getId());
					bankAccountService.saveSavingBankAccount(Math.random()*120000, 5.5, customer.getId());

				} catch (CustomerNotFoundException e) {
					e.printStackTrace();
				}
			});
			List<BankAccountDTO> bankAccounts =bankAccountService.bankAccountList();

			for(BankAccountDTO bankAccount:bankAccounts) {
				for (int i = 0; i < 10; i++) {
					String accountId;
					if(bankAccount instanceof SavingBankAccountDTO){
						accountId= ((SavingBankAccountDTO) bankAccount).getId();
					}
					else{
						accountId= ((CurrentBankAccountDTO) bankAccount).getId();

					}
					bankAccountService.credit(accountId, 10000 + Math.random() * 120000, "Credit");
					bankAccountService.debit(accountId, 1000 + Math.random() * 9000, "Debit");

				}
			}

			/*customerRepository.findAll().forEach(cust->{

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
				});*/
		};
	}

}
