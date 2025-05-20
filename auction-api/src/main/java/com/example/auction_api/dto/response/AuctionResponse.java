package com.example.auction_api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AuctionResponse(
        Long id,
        String title,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime endTime,
        String username,
        String categoryName,
        String auctionImgUrl,
        BigDecimal actualPrice
) {
}
