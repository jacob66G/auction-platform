package com.example.auction_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AuctionResponse(
        Long id,
        String title,
        LocalDateTime endTime,
        String username,
        String categoryName,
        String auctionImgUrl,
        Integer watcherCount,
        BigDecimal actualPrice
) {
}
