package com.example.bank_management.service;

import com.example.bank_management.dto.BankAccountDto;
import com.example.bank_management.dto.BankMapper;
import com.example.bank_management.entity.BankAccountEntity;
import com.example.bank_management.entity.CustomerEntity;
import com.example.bank_management.enums.AccountStatus;
import com.example.bank_management.enums.AccountType;
import com.example.bank_management.exception.AccountNotFoundException;
import com.example.bank_management.exception.CustomerNotFoundException;
import com.example.bank_management.exception.InterestMustBeZeroException;
import com.example.bank_management.repository.BankAccountRepository;
import com.example.bank_management.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BankAccountServiceV2 {

    private final BankAccountRepository bankAccountRepository;
    private final CustomerRepository customerRepository;
    private final BankMapper bankMapper;

    public List<BankAccountEntity> searchAccountType(AccountType accountType) {
        return bankAccountRepository.findAll().stream()
                .filter(account -> account.getAccountType() == accountType).toList();
    }

    public BankAccountDto getAccount(Long id) {
        return bankMapper.toDTO(bankAccountRepository.findById(id).orElseThrow(()->new AccountNotFoundException("Account not found")));
    }

    public BankAccountEntity createAccountV2(BankAccountEntity bankAccountEntity) {
        Long customerId = bankAccountEntity.getCustomer().getCustomerId();
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        // Set default interestRate for SAVINGS if not provided
        if (bankAccountEntity.getAccountType() == AccountType.SAVINGS && bankAccountEntity.getInterestRate() == 0) {
            bankAccountEntity.setInterestRate(3.5);
        } else if (bankAccountEntity.getAccountType() == AccountType.CURRENT && bankAccountEntity.getInterestRate() != 0) {
            throw new InterestMustBeZeroException("Interest rate must be 0 for CURRENT accounts");
        }

        bankAccountEntity.setCustomer(customer);
        bankAccountEntity.setAccountStatus(AccountStatus.ACTIVE);

        return bankAccountRepository.save(bankAccountEntity);
    }

}
