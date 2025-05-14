package com.example.auction_api.dto.request;

import java.math.BigDecimal;

public record UserRequest(
        String username,
        String password,
        String email,
        BigDecimal balance
) {
}
