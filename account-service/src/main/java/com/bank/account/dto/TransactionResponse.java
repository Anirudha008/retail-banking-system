package com.bank.account.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionResponse {

    private Long accountId;

    private String accountNumber;

    private String transactionType;

    private BigDecimal amount;

    private BigDecimal previousBalance;

    private BigDecimal newBalance;

    private String status;

    String message;

}
