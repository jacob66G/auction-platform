package com.example.auction_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AuctionDetailsResponse(
        Long id,
        String title,
        String description,
        Double startingPrice,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String auctionStatus,
        String username,
        String categoryName,
        List<String> auctionImgsUrls,
        Integer watcherCount,
        BigDecimal actualPrice,
        List<BidResponse> bids
) {
}
