package com.example.auction_api.service;

import com.example.auction_api.entity.User;

public interface AuthenticationService {
    User getAuthenticatedUser();
}
