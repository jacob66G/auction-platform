package com.example.auction_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegisterRequest(
        @NotBlank(message = "Username must not be blank")
        @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
        @Pattern(message = "Username contains invalid characters", regexp = "^[a-zA-Z0-9]+$")
        String username,

        @NotBlank(message = "Password must not be blank")
        @Size(min = 5, message = "Password must contains at last 5 characters")
        String password,

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email contains invalid format")
        String email
) {
}
