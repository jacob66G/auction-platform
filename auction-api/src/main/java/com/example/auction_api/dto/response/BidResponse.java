package com.example.auction_api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BidResponse(
        Long id,
        BigDecimal amount,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime bidDate,
        String username
) {
}
