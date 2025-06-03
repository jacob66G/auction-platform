package com.example.auction_api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AuctionCreateResponse(
        Long id,
        String title,
        String description,
        Double startingPrice,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime startTime,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime endTime,
        String auctionStatus,
        String username,
        String categoryName,
        List<String> auctionImgsUrls,
        String message
) {
}
