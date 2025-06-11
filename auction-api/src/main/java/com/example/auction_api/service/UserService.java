package com.example.auction_api.service;

import com.example.auction_api.dto.request.ChangePasswordRequest;
import com.example.auction_api.dto.request.DepositRequest;
import com.example.auction_api.dto.request.UserRegisterRequest;
import com.example.auction_api.dto.request.ChangeEmailRequest;
import com.example.auction_api.dto.response.UserDetailsResponse;
import com.example.auction_api.dto.response.UserResponse;

import java.util.List;


public interface UserService {
    List<UserResponse> getUsersByRole(String role);
    List<UserResponse> getUsersByUsernameOrEmail(String usernameOrEmail);
    UserDetailsResponse getUser();
    UserResponse registerUser(UserRegisterRequest user);
    void changeEmail(ChangeEmailRequest request);
    void deposit(DepositRequest request);
    void deleteUser(Long id);
    void changePassword(ChangePasswordRequest request);
    UserResponse assignModeratorRole(Long userId);
    UserResponse revokeModeratorRole(Long userId);
}
