package com.example.auction_api.mapper;

import com.example.auction_api.dto.response.UserResponse;
import com.example.auction_api.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getBalance()
        );
    }
}
