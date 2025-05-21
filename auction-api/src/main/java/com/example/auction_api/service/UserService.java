package com.example.auction_api.service;

import com.example.auction_api.dto.request.ChangePasswordRequest;
import com.example.auction_api.dto.request.DepositRequest;
import com.example.auction_api.dto.request.UserRegisterRequest;
import com.example.auction_api.dto.request.UserRequest;
import com.example.auction_api.dto.response.UserResponse;
import com.example.auction_api.entity.User;

import java.math.BigDecimal;


public interface UserService {
    UserResponse getUser();
    UserResponse registerUser(UserRegisterRequest user);
    UserResponse updateUser(UserRequest user);
    void deposit(DepositRequest request);
    void deleteUser(Long id);
    void changePassword(ChangePasswordRequest request);
    void refund(BigDecimal amount, User user);
    void decreaseBalance(BigDecimal amount, User user);
}
