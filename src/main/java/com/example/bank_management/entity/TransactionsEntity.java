package com.example.bank_management.entity;

import com.example.bank_management.enums.TransactionsStatus;
import com.example.bank_management.enums.TransactionsType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class) // @CreatedDate and @UpdateTimestamp
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions")
public class TransactionsEntity {


    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "transaction_id", updatable = false, nullable = false)
    private UUID transactionId;

    @Column(name = "from_account_id")
    private Long fromAccountId;

    @Column(name = "to_account_id")
    private Long toAccountId;

    @Column(name = "amount")
    private double amount;

    @Column(name = "initial_balance")
    private double initialBalance;

    @Column(name = "remaining_balance")
    private double remainingBalance;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private TransactionsType transactionsType;

    @CreatedDate
    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TransactionsStatus transactionsStatus;

}
