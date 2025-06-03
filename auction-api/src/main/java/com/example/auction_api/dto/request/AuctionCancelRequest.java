package com.example.auction_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuctionCancelRequest(
        @NotBlank(message = "Reason cannot be empty")
        @Size(max = 255, message = "Reason cannot have more than 255 characters")
        String reason
) {
}
