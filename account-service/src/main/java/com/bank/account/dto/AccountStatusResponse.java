package com.bank.account.dto;

import lombok.Data;

@Data
public class AccountStatusResponse {

    private Long accountId;

    private String status;

    private String message;
}