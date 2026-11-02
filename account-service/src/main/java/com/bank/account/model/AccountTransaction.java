package com.bank.account.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_transactions")
@Data
public class AccountTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private String transactionType;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal previousBalance;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal newBalance;

    @Column(nullable = false)
    private String status;

    private String description;

    @Column(nullable = false)
    private LocalDateTime transactionDate;
}