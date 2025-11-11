package com.example.bank_management.controller;

import com.example.bank_management.dto.BankMapper;
import com.example.bank_management.dto.TransactionsDto;
import com.example.bank_management.service.TransactionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionsController {

    private final TransactionsService transactionsService;
    private final BankMapper bankMapper;

    @GetMapping("/")
    public List<TransactionsDto> getAllTransactions() {
        return transactionsService.getAllTransactions();
    }

    @GetMapping("/{id}")
    public TransactionsDto getTransaction(@PathVariable UUID id) {
        return transactionsService.getTransaction(id);
    }


}
