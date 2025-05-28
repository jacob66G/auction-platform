package com.example.auction_api.event;

import com.example.auction_api.entity.User;

public record UserRegistrationEvent(User user) {
}
