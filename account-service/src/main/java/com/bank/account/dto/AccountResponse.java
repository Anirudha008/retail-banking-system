package com.bank.account.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountResponse {

    private Long accountId;

    private Long customerId;

    private String accountNumber;

    private String accountType;

    private BigDecimal balance;

    private String status;

    private String message;
}