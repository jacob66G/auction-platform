package com.example.auction_api.dto.request;

import jakarta.validation.constraints.Size;

public record AuctionRequestComment(
        @Size(max = 255, message = "Comment cannot have more than 255 characters")
        String comment
) {
}

