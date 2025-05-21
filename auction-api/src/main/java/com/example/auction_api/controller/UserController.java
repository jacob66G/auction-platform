package com.example.auction_api.controller;

import com.example.auction_api.dto.request.ChangePasswordRequest;
import com.example.auction_api.dto.request.DepositRequest;
import com.example.auction_api.dto.request.UserRequest;
import com.example.auction_api.dto.response.UserResponse;
import com.example.auction_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok().body(userService.getUser());
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok("Change password successful");
    }

    @PutMapping("/deposit")
    public ResponseEntity<String> changeBalance(@Valid @RequestBody DepositRequest request) {
        userService.deposit(request);
        return ResponseEntity.ok("Deposit successful");
    }

    @PutMapping("/change-personal-data")
    public ResponseEntity<UserResponse> changePersonalData(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok().body(userService.updateUser(request));
    }
}
