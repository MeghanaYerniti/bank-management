package com.example.bank_management.controller;

import com.example.bank_management.dto.BankMapper;
import com.example.bank_management.dto.CustomerDto;
import com.example.bank_management.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final BankMapper bankMapper;

    @GetMapping("/")
    public List<CustomerDto> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @PostMapping("/")
    public CustomerDto createCustomer(@Valid @RequestBody CustomerDto customerDTO) {
        return customerService.createCustomer(customerDTO);
    }

    @PutMapping("/{id}")
    public CustomerDto updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerDto customerDTO) {
        return customerService.updateCustomer(id, customerDTO);
    }

    @GetMapping("/{id}")
    public CustomerDto getCustomer(@PathVariable Long id) {
        return customerService.getCustomer(id);
    }

}