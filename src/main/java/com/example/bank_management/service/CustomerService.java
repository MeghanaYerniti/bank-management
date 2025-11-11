package com.example.bank_management.service;

import com.example.bank_management.dto.BankMapper;
import com.example.bank_management.dto.CustomerDto;
import com.example.bank_management.entity.BankAccountEntity;
import com.example.bank_management.entity.CustomerEntity;
import com.example.bank_management.enums.AccountStatus;
import com.example.bank_management.exception.CustomerNotFoundException;
import com.example.bank_management.repository.CustomerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CustomerService {

    private final BankMapper bankMapper;
    private final CustomerRepository customerRepository;

    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(bankMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CustomerDto createCustomer(@Valid CustomerDto customerDTO) {
        CustomerEntity customerEntity = bankMapper.toEntity(customerDTO);
        if (customerEntity.getAccounts() != null) {
            customerEntity.getAccounts().forEach(account -> {
                account.setCustomer(customerEntity);
                if (account.getCreatedAt() == null) account.setCreatedAt(LocalDateTime.now());
                if (account.getUpdatedAt() == null) account.setUpdatedAt(LocalDateTime.now());
                if (account.getAccountStatus() == null) account.setAccountStatus(AccountStatus.ACTIVE);
            });
        }
        if (customerEntity.getCreatedAt() == null) customerEntity.setCreatedAt(LocalDateTime.now());
        if (customerEntity.getUpdatedAt() == null) customerEntity.setUpdatedAt(LocalDateTime.now());
        CustomerEntity savedCustomer = customerRepository.save(customerEntity); // Save customer first
        if (customerEntity.getAccounts() != null) {
            customerEntity.getAccounts().forEach(account -> {
                if (account.getAccountId() < 10000000) {
                    throw new RuntimeException("Account ID must be at least 8 digits");
                }
                account.setCustomer(savedCustomer);
            });
        }
        return bankMapper.toDTO(savedCustomer);
    }

    public CustomerDto updateCustomer(Long id, CustomerDto updatedCustomer) {
//        CustomerEntity entity = bankMapper.toEntity(customerDTO);
//        entity.setCustomerId(id);
        CustomerEntity existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        if (updatedCustomer.getName() != null && !updatedCustomer.getName().isBlank()) {
            existingCustomer.setName(updatedCustomer.getName());
        }
        if (updatedCustomer.getPan() != null && !updatedCustomer.getPan().isBlank()) {
            existingCustomer.setPan(updatedCustomer.getPan());
        }
        if (updatedCustomer.getEmail() != null && !updatedCustomer.getEmail().isBlank()) {
            existingCustomer.setEmail(updatedCustomer.getEmail());
        }
        if (updatedCustomer.getPhone() != null && !updatedCustomer.getPhone().isBlank()) {
            existingCustomer.setPhone(updatedCustomer.getPhone());
        }
        // updating bankAccounts table
        for (BankAccountEntity account : existingCustomer.getAccounts()) {
            account.setAccountHolderName(existingCustomer.getName());
        }

        return bankMapper.toDTO(customerRepository.save(existingCustomer));
    }

    public CustomerDto getCustomer(Long id) {
        return bankMapper.toDTO(customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found")));
    }

}
