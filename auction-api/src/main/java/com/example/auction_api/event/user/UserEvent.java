package com.example.auction_api.event.user;

import lombok.Getter;

@Getter
public abstract class UserEvent {

    private final String userEmail;
    private final String username;

    public UserEvent(String userEmail, String username) {
        this.userEmail = userEmail;
        this.username = username;
    }
}
