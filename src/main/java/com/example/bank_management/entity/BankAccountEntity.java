package com.example.bank_management.entity;

import com.example.bank_management.enums.AccountStatus;
import com.example.bank_management.enums.AccountType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "accountId"
) // resolve infinite loop
@Entity
@EntityListeners(AuditingEntityListener.class) // @CreatedDate and @UpdateTimestamp
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bank_account")
public class BankAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq") // auto generation
    @SequenceGenerator(
            name = "account_seq",
            sequenceName = "account_sequence",
            initialValue = 10000000,
            allocationSize = 1
    )
    @Column(name = "account_id")
    private Long accountId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false) // specify foreign key
//    @JsonBackReference
    private CustomerEntity customer;

    @Column(name = "account_holder_name")
    private String accountHolderName;

    @Enumerated(EnumType.STRING) // specify enum type
    @Column(name = "account_type")
    private AccountType accountType;

    @Column(name = "balance")
    private double balance;

    @Column(name = "interest_rate")
    private double interestRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status")
    private AccountStatus accountStatus;

    @Column(name = "last_transaction_timestamp")
    private LocalDateTime lastTransactionTimestamp;

    @CreatedDate // update time at creation
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate  // update time at each modification
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}