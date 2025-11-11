package com.example.bank_management.exception;

public class RequiresPanException extends RuntimeException {
    public RequiresPanException(String message) {
        super(message);
    }
}