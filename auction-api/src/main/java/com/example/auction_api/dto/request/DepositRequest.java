package com.example.auction_api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DepositRequest(
        @NotNull(message = "Bid amount must not be null")
        @Min(value = 0, message = "Deposit price must be at least 0")
        BigDecimal amount
) {
}
