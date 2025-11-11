package com.example.bank_management.repository;

import com.example.bank_management.entity.TransactionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionsRepository extends JpaRepository<TransactionsEntity, UUID> {
}
