package com.bank.account.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferResponse {

    private Long transferId;

    private Long sourceAccountId;
    private Long targetAccountId;

    private BigDecimal amount;

    private BigDecimal sourcePreviousBalance;
    private BigDecimal sourceNewBalance;

    private BigDecimal targetPreviousBalance;
    private BigDecimal targetNewBalance;

    private String status;
    private String message;

}
