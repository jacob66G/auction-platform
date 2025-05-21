package com.example.auction_api.service.Impl;

import com.example.auction_api.dao.UserDao;
import com.example.auction_api.entity.User;
import com.example.auction_api.exception.ResourceNotFoundException;
import com.example.auction_api.service.AuthenticationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserDao userDao;

    public AuthenticationServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userDao.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException(User.class.getSimpleName(), username));
    }
}
