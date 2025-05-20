package com.example.auction_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank(message = "old password must not be blank")
        String oldPassword,

        @NotBlank(message = "new password must not be blank")
        String newPassword
) {
}
