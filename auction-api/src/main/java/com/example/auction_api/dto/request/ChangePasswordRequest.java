package com.example.auction_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "old password must not be blank")
        String oldPassword,

        @NotBlank(message = "new password must not be blank")
        @Size(min = 5, message = "Password must contains at last 5 characters")
        String newPassword
) {
}
