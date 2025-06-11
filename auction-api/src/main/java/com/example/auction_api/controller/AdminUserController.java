package com.example.auction_api.controller;

import com.example.auction_api.dto.response.UserResponse;
import com.example.auction_api.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/role")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@RequestParam(defaultValue = "user") String role) {
        return ResponseEntity.ok().body(userService.getUsersByRole(role));
    }

    @GetMapping()
    public ResponseEntity<List<UserResponse>> getUsersByUsernameOrEmail(@RequestParam String user) {
        return ResponseEntity.ok().body(userService.getUsersByUsernameOrEmail(user));
    }

    @PutMapping("/{userId}/assign-moderator")
    public ResponseEntity<UserResponse> assignModeratorRole(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.assignModeratorRole(userId));
    }

    @PutMapping("/{userId}/revoke-moderator")
    public ResponseEntity<UserResponse> revokeModeratorRole(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.revokeModeratorRole(userId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
