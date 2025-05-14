package com.example.auction_api.dto.response;

import com.example.auction_api.dto.enums.UserRole;

import java.math.BigDecimal;

public record UserResponse(
        Long id,
        String username,
        String email,
        UserRole role,
        BigDecimal balance
) {
}
