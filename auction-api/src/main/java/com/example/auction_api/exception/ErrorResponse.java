package com.example.auction_api.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        int Status,
        String message,
        String details,
        LocalDateTime timestamp
) {
}
