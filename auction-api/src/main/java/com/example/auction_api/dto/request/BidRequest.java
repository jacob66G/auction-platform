package com.example.auction_api.dto.request;

import java.math.BigDecimal;

public record BidRequest(
        BigDecimal amount,
        Long auctionId
) {
}
