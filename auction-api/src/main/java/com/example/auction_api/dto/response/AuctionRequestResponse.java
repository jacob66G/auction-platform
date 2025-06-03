package com.example.auction_api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record AuctionRequestResponse(
        Long id,
        String auctionTitle,
        String requestType,
        String requestStatus,
        String reason,
        String comment,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime requestDate,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime decisionDate,
        String requesterName,
        String moderatorName
) {
}
