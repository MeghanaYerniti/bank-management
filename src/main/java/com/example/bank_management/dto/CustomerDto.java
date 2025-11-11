package com.example.bank_management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CustomerDto {

    private Long customerId;

    @NotBlank(message = "Name is required")
    @Size(min = 5, max = 100, message = "Name must be between 5 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name must not contain special characters and numbers")
    private String name;

    @NotBlank(message = "PAN is required")
    @Size(min = 8, message = "PAN must be at least 8 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "PAN must not contain special characters")
    private String pan;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(?!(\\d)\\1{9})(\\+91)?[6-9]\\d{9}$", message = "Invalid Indian phone number")
    private String phone;

    private List<BankAccountDto> accounts;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}