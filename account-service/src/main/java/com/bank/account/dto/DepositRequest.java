package com.bank.account.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Negative;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositRequest {
    @NotNull(message = "Deposit is required")
    @DecimalMin(value = "0.00", inclusive = false,
            message = "Amount must be greater than zero")
    BigDecimal amount;

    String description;
}
