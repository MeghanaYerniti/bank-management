package com.example.bank_management.dto;

import com.example.bank_management.entity.BankAccountEntity;
import com.example.bank_management.entity.CustomerEntity;
import com.example.bank_management.entity.TransactionsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BankMapper {
    @Mapping(target = "customer.accounts", ignore = true)
    BankAccountDto toDTO(BankAccountEntity bankAccountEntity);
    BankAccountEntity toEntity(BankAccountDto bankAccountDTO);

    CustomerDto toDTO(CustomerEntity customerEntity);
    CustomerEntity toEntity(CustomerDto customerDto);

    TransactionsDto toDTO(TransactionsEntity transactionsEntity);
    TransactionsEntity toEntity(TransactionsDto transactionsDto);
}
