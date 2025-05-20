package com.example.auction_api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AuctionRequest(
        @NotBlank(message = "Title must not be blank")
        @Size(min = 5, max = 30, message = "Title must be between 5 and 30 characters")
        String title,

        @NotBlank(message = "Description must not be blank")
        @Size(min = 30, max = 255, message = "Description must be between 30 and 255 characters")
        String description,

        @NotNull(message = "Bid amount must not be null")
        @Min(value = 0, message = "Starting price must be at least 0")
        BigDecimal startingPrice,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @NotNull(message = "Start time must not be null")
        @FutureOrPresent(message = "Start time must be in the present or future")
        LocalDateTime startTime,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @NotNull(message = "End time must not be null")
        @Future(message = "End time must be in the future")
        LocalDateTime endTime,

        @NotNull(message = "Category ID must not be null")
        Long categoryId
) {
}
