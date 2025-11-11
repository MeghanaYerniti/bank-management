package com.example.bank_management.exception;

public class ClosedAccountException extends RuntimeException{
    public ClosedAccountException(String message) {
        super(message);
    }
}
