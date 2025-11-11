package com.example.bank_management.exception;

public class InterestMustBeZeroException extends RuntimeException {
    public InterestMustBeZeroException(String message) {
        super(message);
    }
}
