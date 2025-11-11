package com.example.bank_management.service;

import com.example.bank_management.dto.BankAccountDto;
import com.example.bank_management.dto.BankMapper;
import com.example.bank_management.entity.BankAccountEntity;
import com.example.bank_management.entity.CustomerEntity;
import com.example.bank_management.entity.TransactionsEntity;
import com.example.bank_management.enums.AccountStatus;
import com.example.bank_management.enums.AccountType;
import com.example.bank_management.enums.TransactionsStatus;
import com.example.bank_management.enums.TransactionsType;
import com.example.bank_management.exception.*;
import com.example.bank_management.repository.BankAccountRepository;
import com.example.bank_management.repository.CustomerRepository;
import com.example.bank_management.repository.TransactionsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final TransactionsRepository transactionsRepository;
    private final CustomerRepository customerRepository;
    private final BankMapper bankMapper;

    public BankAccountDto createAccount(BankAccountDto bankAccountDto) {

        BankAccountEntity bankAccountEntity = bankMapper.toEntity(bankAccountDto);
        // log
        //System.out.println("Incoming AccountType from DTO: " + bankAccountDto.getAccountType());
        Long customerId = bankAccountEntity.getCustomer().getCustomerId();
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        if (bankAccountEntity.getAccountType() == AccountType.SAVINGS) {
            if (bankAccountEntity.getInterestRate() < 3.5) {
                bankAccountEntity.setInterestRate(3.5);
            }
            bankAccountEntity.setAccountType(AccountType.SAVINGS);
        } else if (bankAccountEntity.getAccountType() == AccountType.CURRENT) {
            if (bankAccountEntity.getInterestRate() != 0) {
                throw new InterestMustBeZeroException("Interest rate must be 0 for CURRENT accounts");
            }
            bankAccountEntity.setAccountType(AccountType.CURRENT);
        }
        if (bankAccountEntity.getLastTransactionTimestamp() == null) {
            bankAccountEntity.setLastTransactionTimestamp(LocalDateTime.now());
        }
        bankAccountEntity.setCustomer(customer);
        bankAccountEntity.setAccountStatus(AccountStatus.ACTIVE);
//        bankAccountEntity.setAccountType(bankAccountEntity.getAccountType());

        BankAccountEntity save = bankAccountRepository.save(bankAccountEntity);

        return bankMapper.toDTO(save);
    }

    public BankAccountDto getAccount(Long id) {
        return bankMapper.toDTO(bankAccountRepository.findById(id).orElseThrow(()->new AccountNotFoundException("Account not found")));
    }

    public double getAccountBalance(Long id) {
        BankAccountEntity account = bankAccountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        return account.getBalance();
    }

    @Transactional  // single transaction — either all succeed, or all fail
    public BankAccountDto depositAmount(Long id, double amount) {

        BankAccountEntity account = bankAccountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (account.getAccountStatus() == AccountStatus.CLOSED) {
            throw new ClosedAccountException("Can't perform deposit transaction since account is closed");
        }

        if (amount > 100000) {
            CustomerEntity customer = account.getCustomer();
            if (customer.getPan() == null || customer.getPan().isBlank()) {
                throw new RequiresPanException("PAN is required for deposits exceeding 100,000");
            }
            if (amount > 200000) {
                throw new HighValueTransactionException("Deposit exceeds 200,000 limit");
            }
        }
        double newBalance = account.getBalance() + amount;
        if (newBalance < 0) {
            throw new MinimumBalanceException("Deposit cannot result in a negative balance");
        }
        TransactionsEntity transaction = new TransactionsEntity();
        //transaction.setToAccountId(id);
        transaction.setAmount(amount);
        transaction.setInitialBalance(account.getBalance());
        transaction.setRemainingBalance(newBalance);
        transaction.setTransactionsType(TransactionsType.DEPOSIT);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setTransactionsStatus(TransactionsStatus.SUCCESS);
        transactionsRepository.save(transaction);

        account.setBalance(newBalance);
        account.setLastTransactionTimestamp(LocalDateTime.now());
        return bankMapper.toDTO(bankAccountRepository.save(account));
    }

    @Transactional
    public BankAccountDto withdrawAmount(Long id, double amount) {
        BankAccountEntity account = bankAccountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (account.getAccountStatus() == AccountStatus.CLOSED) {
            throw new ClosedAccountException("Can't perform deposit transaction since account is closed");
        }
        if (account.getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient funds for withdrawal");
        }
        if (account.getAccountType() == AccountType.SAVINGS && (account.getBalance() - amount) < 1000) {
            throw new MinimumBalanceException("SAVINGS account must maintain a minimum balance of 1000");
        }
        if (account.getAccountType() == AccountType.CURRENT && (account.getBalance() - amount) < -5000) {
            throw new InsufficientFundsException("CURRENT account cannot go below -5000 overdraft limit");
        }
        double newBalance = account.getBalance() - amount;
        if (newBalance < 0 && account.getAccountType() == AccountType.SAVINGS) {
            throw new MinimumBalanceException("SAVINGS account cannot have a negative balance");
        }

        TransactionsEntity transaction = new TransactionsEntity();
//        transaction.setTransactionId(UUID.randomUUID());
        transaction.setFromAccountId(id);
        transaction.setAmount(amount);
        transaction.setInitialBalance(account.getBalance());
        transaction.setRemainingBalance(newBalance);
        transaction.setTransactionsType(TransactionsType.WITHDRAW);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setTransactionsStatus(TransactionsStatus.SUCCESS);
        transactionsRepository.save(transaction);

        account.setBalance(newBalance);
        account.setLastTransactionTimestamp(LocalDateTime.now());
        return bankMapper.toDTO(bankAccountRepository.save(account));
    }

    public void deleteAccount(Long id) {
        Optional<BankAccountEntity> account= bankAccountRepository.findById(id);
        if(account.isEmpty()){
            throw new AccountNotFoundException("Account not found");
        }
        else {
            bankAccountRepository.deleteById(id);
        }
    }

    public List<BankAccountDto> getAllAccounts() {
        return bankAccountRepository.findAll()
                .stream()
                .map(bankMapper::toDTO)
                .collect(Collectors.toList());
    }

//    public Page<BankAccountDto> getAllAccounts(Pageable pageable) {
//        return bankAccountRepository.findAll(pageable)
//                .map(bankMapper::toDTO);
//    }

    public List<BankAccountDto> getTopBalances(int limit) {
        return bankAccountRepository.findAll().stream()
                .sorted(Comparator.comparingDouble(BankAccountEntity::getBalance).reversed())
                .limit(limit)
                .map(bankMapper::toDTO)
                .collect(Collectors.toList());
    }

//    public List<BankAccountEntity> searchAccountType(AccountType accountType) {
//        return bankAccountRepository.findAll().stream()
//                .filter(account -> account.getType() == accountType).toList();
//    }


}
