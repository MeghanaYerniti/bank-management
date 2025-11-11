package com.example.bank_management.exception;

public class ZeroBalanceException extends RuntimeException {
    public ZeroBalanceException(String message) {
        super(message);
    }
}
