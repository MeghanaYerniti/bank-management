package com.example.bank_management.dto;

import com.example.bank_management.enums.AccountStatus;
import com.example.bank_management.enums.AccountType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BankAccountDto {

    @Positive
    @Min(value = 10000000L, message = "Account ID must be at least 8 digits")
    private Long accountId;

    private CustomerDto customer;

    @NotBlank(message = "Account holder name is required")
    @Size(min = 5, max = 50, message = "Account holder name must be between 5 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Account holder name must not contain numbers or special characters")
    private String accountHolderName;

    private AccountType accountType;

    @PositiveOrZero(message = "Balance must be non-negative")
    private double balance;

    @PositiveOrZero(message = "Interest rate must be non-negative")
    private double interestRate;

    @NotNull(message = "Account status is required")
    private AccountStatus accountStatus;

    private LocalDateTime lastTransactionTimestamp;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}