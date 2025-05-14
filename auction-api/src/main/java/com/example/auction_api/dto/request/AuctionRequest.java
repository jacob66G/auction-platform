package com.example.auction_api.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AuctionRequest(
        String title,
        String description,
        BigDecimal startingPrice,
        LocalDateTime startTime,
        LocalDateTime  endTime,
        Long categoryId
) {
}
