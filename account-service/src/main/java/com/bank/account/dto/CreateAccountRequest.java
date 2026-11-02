package com.bank.account.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateAccountRequest {

    @NotNull(message = "Customer ID is required")
    @Positive(message = "Customer ID must be greater than zero")
    private Long customerId;

    @NotBlank(message = "Account type is required")
    @Pattern(
            regexp = "SAVINGS|CURRENT",
            message = "Account type must be SAVINGS or CURRENT"
    )
    private String accountType;

    @NotNull(message = "Initial deposit is required")
    @DecimalMin(
            value = "0.00",
            inclusive = false,
            message = "Initial deposit must be greater than zero"
    )
    private BigDecimal initialDeposit;
}