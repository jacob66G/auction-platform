package com.example.auction_api.event.user;


public class UserRegistrationEvent extends UserEvent {

    public UserRegistrationEvent(String userEmail, String username) {
        super(userEmail, username);
    }
}
