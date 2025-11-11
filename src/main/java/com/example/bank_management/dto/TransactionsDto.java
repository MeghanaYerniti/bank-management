package com.example.bank_management.dto;

import com.example.bank_management.enums.TransactionsStatus;
import com.example.bank_management.enums.TransactionsType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionsDto {

    private UUID transactionId;

    private Long fromAccountId;

    private Long toAccountId;

    @Positive(message = "Amount must be greater than 0")
    private double amount;

    private double initialBalance;

    private double remainingBalance;

    @NotNull(message = "Transaction type is required")
    private TransactionsType transactionsType;

    private LocalDateTime timestamp;

    @NotNull(message = "Status is required")
    private TransactionsStatus transactionsStatus;
}