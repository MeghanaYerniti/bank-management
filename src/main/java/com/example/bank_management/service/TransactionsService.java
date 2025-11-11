package com.example.bank_management.service;

import com.example.bank_management.dto.BankMapper;
import com.example.bank_management.dto.TransactionsDto;
import com.example.bank_management.entity.BankAccountEntity;
import com.example.bank_management.entity.CustomerEntity;
import com.example.bank_management.entity.TransactionsEntity;
import com.example.bank_management.enums.AccountStatus;
import com.example.bank_management.enums.AccountType;
import com.example.bank_management.enums.TransactionsStatus;
import com.example.bank_management.enums.TransactionsType;
import com.example.bank_management.exception.*;
import com.example.bank_management.repository.BankAccountRepository;
import com.example.bank_management.repository.TransactionsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TransactionsService {

    private final BankAccountRepository bankAccountRepository;
    private final TransactionsRepository transactionsRepository;
    private final BankMapper bankMapper;

    @Transactional
    public TransactionsDto transfer(Long fromAccountId, Long toAccountId, double amount) {
        BankAccountEntity fromAccount = bankAccountRepository.findById(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException("From account not found"));
        BankAccountEntity toAccount = bankAccountRepository.findById(toAccountId)
                .orElseThrow(() -> new AccountNotFoundException("To account not found"));

        if (fromAccount.getAccountStatus() == AccountStatus.CLOSED) {
            throw new ClosedAccountException("Can't perform deposit transaction since account is closed");
        }
        if (toAccount.getAccountStatus() == AccountStatus.CLOSED) {
            throw new ClosedAccountException("Can't perform deposit transaction since account is closed");
        }

        if (fromAccount.getBalance() < amount) {
            TransactionsEntity failedTransaction = bankMapper.toEntity(createFailedTransaction(fromAccountId, toAccountId, amount));
            transactionsRepository.save(failedTransaction);
            throw new InsufficientFundsException("Insufficient funds for transfer");
        }
        if (fromAccount.getAccountType() == AccountType.SAVINGS && (fromAccount.getBalance() - amount) < 1000) {
            TransactionsEntity failedTransaction = bankMapper.toEntity(createFailedTransaction(fromAccountId, toAccountId, amount));
            transactionsRepository.save(failedTransaction);
            throw new MinimumBalanceException("SAVINGS account must maintain a minimum balance of 1000");
        }
        // High-value transfer checks
        if (amount > 100000) {
            CustomerEntity customer = fromAccount.getCustomer();
            if (customer.getPan() == null || customer.getPan().isBlank()) {
                TransactionsEntity failedTransaction = bankMapper.toEntity(createFailedTransaction(fromAccountId, toAccountId, amount));
                transactionsRepository.save(failedTransaction);
                throw new RequiresPanException("PAN is required for transfers exceeding 100,000");
            }
            if (amount > 200000) {
                TransactionsEntity failedTransaction = bankMapper.toEntity(createFailedTransaction(fromAccountId, toAccountId, amount));
                transactionsRepository.save(failedTransaction);
                throw new HighValueTransactionException("Transfer exceeds 200,000 limit");
            }
        }
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);
        fromAccount.setLastTransactionTimestamp(LocalDateTime.now());
        toAccount.setLastTransactionTimestamp(LocalDateTime.now());
        bankAccountRepository.save(fromAccount);
        bankAccountRepository.save(toAccount);

        TransactionsEntity transaction = new TransactionsEntity();
        transaction.setFromAccountId(fromAccountId);
        transaction.setToAccountId(toAccountId);
        transaction.setAmount(amount);
        transaction.setInitialBalance(fromAccount.getBalance() + amount);
        transaction.setRemainingBalance(fromAccount.getBalance());
        transaction.setTransactionsType(TransactionsType.TRANSFER);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setTransactionsStatus(TransactionsStatus.SUCCESS);
        return bankMapper.toDTO(transactionsRepository.save(transaction));
    }

    private TransactionsDto createFailedTransaction(Long fromAccountId, Long toAccountId, double amount) {
        TransactionsEntity transaction = new TransactionsEntity();
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setFromAccountId(fromAccountId);
        transaction.setToAccountId(toAccountId);
        transaction.setAmount(amount);
        transaction.setInitialBalance(bankAccountRepository.findById(fromAccountId)
                .map(BankAccountEntity::getBalance).orElse(0.0));
        transaction.setRemainingBalance(transaction.getInitialBalance());
        transaction.setTransactionsType(TransactionsType.TRANSFER);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setTransactionsStatus(TransactionsStatus.FAILED);
        return bankMapper.toDTO(transaction);
    }

    public List<TransactionsDto> getAllTransactions() {

        return transactionsRepository.findAll()
                .stream()
                .map(bankMapper::toDTO)
                .collect(Collectors.toList());
    }

    public TransactionsDto getTransaction(UUID id) {
        return bankMapper.toDTO(transactionsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found")));
    }

    public List<TransactionsDto> getTransactionsByAccountId(Long accountId) {
        List<TransactionsEntity> transactions = transactionsRepository.findAll().stream()
                .filter(t -> (t.getFromAccountId() != null && t.getFromAccountId().equals(accountId))
                        || (t.getToAccountId() != null && t.getToAccountId().equals(accountId)))
                .toList();
        return transactions.stream()
                .map(bankMapper::toDTO)
                .collect(Collectors.toList());
    }

}
