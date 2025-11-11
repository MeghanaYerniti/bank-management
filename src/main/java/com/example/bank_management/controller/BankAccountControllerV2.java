package com.example.bank_management.controller;

import com.example.bank_management.dto.BankAccountDto;
import com.example.bank_management.dto.BankMapper;
import com.example.bank_management.entity.BankAccountEntity;
import com.example.bank_management.enums.AccountType;
import com.example.bank_management.service.BankAccountService;
import com.example.bank_management.service.BankAccountServiceV2;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/accounts")
@RequiredArgsConstructor
public class BankAccountControllerV2 {

    private final BankAccountServiceV2 bankAccountServiceV2;
    private final BankMapper bankMapper;

    @GetMapping("/search")
    public List<BankAccountDto> searchAccountType(@RequestParam AccountType accountType) {
        return bankAccountServiceV2.searchAccountType(accountType).stream()
                .map(bankMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}/interest")
    public double getInterest(@PathVariable Long id) {
        BankAccountDto account = bankAccountServiceV2.getAccount(id);
        if (account.getAccountType() != AccountType.SAVINGS) {
            throw new RuntimeException("Interest calculation is only for SAVINGS accounts");
        }
        return account.getBalance() * (account.getInterestRate() / 100);
    }

    @PostMapping("/")
    public BankAccountDto createAccountV2(@Valid @RequestBody BankAccountDto bankAccountDTO) {
        BankAccountEntity entity = bankMapper.toEntity(bankAccountDTO);
        BankAccountEntity saved = bankAccountServiceV2.createAccountV2(entity);
        return bankMapper.toDTO(saved);
    }
}
