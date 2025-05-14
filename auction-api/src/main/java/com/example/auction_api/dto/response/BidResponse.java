package com.example.auction_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BidResponse(
        Long id,
        BigDecimal amount,
        LocalDateTime bidDate,
        String username
) {
}
