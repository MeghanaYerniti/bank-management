package com.example.bank_management.exception;

public class HighValueTransactionException extends RuntimeException {
    public HighValueTransactionException(String message) {
        super(message);
    }
}
