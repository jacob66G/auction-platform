package com.example.auction_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Name must not be blank")
        @Size(min = 5, max = 20, message = "Name must be between 5 and 20 characters")
        @Pattern(message = "Name contains invalid characters", regexp = "^[a-zA-Z,.+]+$")
        String name
) {
}
