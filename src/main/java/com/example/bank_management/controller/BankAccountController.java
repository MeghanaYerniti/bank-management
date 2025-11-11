package com.example.bank_management.controller;

import com.example.bank_management.dto.BankAccountDto;
import com.example.bank_management.dto.BankMapper;
import com.example.bank_management.dto.TransactionsDto;
import com.example.bank_management.service.BankAccountService;
import com.example.bank_management.service.TransactionsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private final TransactionsService transactionsService;
    private final BankMapper bankMapper;

    @PostMapping("/")
    public BankAccountDto createAccount(@Valid @RequestBody BankAccountDto bankAccountDto) {
        return bankAccountService.createAccount(bankAccountDto);
    }

    @GetMapping("/{id}")
    public BankAccountDto getAccount(@PathVariable Long id) {
        return bankAccountService.getAccount(id);
    }

    @GetMapping("/{id}/balance")
    public double getBalance(@PathVariable Long id) {
        return bankAccountService.getAccountBalance(id);
    }

    @PutMapping("/{id}/deposit")
    public String deposit(@PathVariable Long id, @RequestParam double amount) {
        BankAccountDto updatedAccount = bankAccountService.depositAmount(id, amount);
        return "Deposited " + amount + ". New Balance: " + updatedAccount.getBalance();
    }

    @PutMapping("/{id}/withdraw")
    public String withdraw(@PathVariable Long id, @RequestParam double amount) {
        BankAccountDto updatedAccount = bankAccountService.withdrawAmount(id, amount);
        return "Withdraw " + amount + " New Balance: " + updatedAccount.getBalance();
    }

    @DeleteMapping("/{id}")
    public String deleteAccount(@PathVariable Long id) {
        bankAccountService.deleteAccount(id);
        return "Deleted account " + id;
    }

    @GetMapping("/all-accounts")
    public List<BankAccountDto> getAllAccounts() {
        return bankAccountService.getAllAccounts();
    }

    // /api/v1/accounts/all-accounts?page=1&size=20
//    @GetMapping("/all-accounts")
//    public Page<BankAccountDto> getAllAccounts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        return bankAccountService.getAllAccounts(pageable);
//    }

    @PutMapping("/transfer")
    public TransactionsDto transfer(@RequestParam Long fromAccountId, @RequestParam Long toAccountId, @RequestParam double amount) {
        return transactionsService.transfer(fromAccountId, toAccountId, amount);
    }

    @GetMapping("/{id}/transactions")
    public List<TransactionsDto> getAccountTransactions(@PathVariable Long id) {
        return transactionsService.getTransactionsByAccountId(id);
    }

    @GetMapping("/report/top-balances")
    public List<BankAccountDto> getTopBalances() {
        return bankAccountService.getTopBalances(5);
    }

}
