package com.bank.account.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {

    @NotNull(message = "Source account ID is required")
    @Positive(message = "Source account ID must be positive")
    private Long sourceAccountId;

    @NotNull(message = "Target account ID is required")
    @Positive(message = "Target account ID must be positive")
    private Long targetAccountId;

    @NotNull(message = "Transfer amount is required")
    @DecimalMin(value = "0.00", inclusive = false,
            message = "Amount must be greater than zero")
    private BigDecimal amount;

    private String description;
}
