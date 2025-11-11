package com.example.bank_management.config;

import com.example.bank_management.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientFundsException(InsufficientFundsException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Insufficient Funds", ex.getMessage(), request);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCustomerNotFoundException(InsufficientFundsException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Customer not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(ClosedAccountException.class)
    public ResponseEntity<Map<String, Object>> handleClosedAccountException(InsufficientFundsException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Can't perform deposit transaction since account is closed", ex.getMessage(), request);
    }

    @ExceptionHandler(InterestMustBeZeroException.class)
    public ResponseEntity<Map<String, Object>> handleInterestMustBeZeroException(InsufficientFundsException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Interest rate must be 0 for CURRENT accounts", ex.getMessage(), request);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAccountNotFoundException(InsufficientFundsException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Account not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(MinimumBalanceException.class)
    public ResponseEntity<Map<String, Object>> handleMinimumBalanceException(MinimumBalanceException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Minimum Balance Violation, Minimum Balance Should Be Greater than Zero", ex.getMessage(), request);
    }

    @ExceptionHandler(ZeroBalanceException.class)
    public ResponseEntity<Map<String, Object>> handleZeroBalanceException(MinimumBalanceException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Deposit cannot result in a negative balance", ex.getMessage(), request);
    }

    @ExceptionHandler(HighValueTransactionException.class)
    public ResponseEntity<Map<String, Object>> handleHighValueTransactionException(HighValueTransactionException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "High Value Transaction", ex.getMessage(), request);
    }

    @ExceptionHandler(RequiresPanException.class)
    public ResponseEntity<Map<String, Object>> handleRequiresPanException(RequiresPanException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "PAN Required", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        StringBuilder message = new StringBuilder("Validation failed: ");
        ex.getBindingResult().getAllErrors().forEach(error -> message.append(error.getDefaultMessage()).append("; "));
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation Error", message.toString(), request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        StringBuilder message = new StringBuilder("Validation failed: ");
        ex.getConstraintViolations().forEach(violation -> message.append(violation.getMessage()).append("; "));
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation Error", message.toString(), request);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Map<String, Object>> handleTransactionSystemException(TransactionSystemException ex, WebRequest request) {
        Throwable cause = ex.getMostSpecificCause();
        String message = cause != null ? cause.getMessage() : "Transaction failed due to system error";
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Transaction Failure", message, request);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error", ex.getMessage(), request);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);
        response.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, status);
    }
}
