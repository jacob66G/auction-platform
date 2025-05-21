package com.example.auction_api.dao;

import com.example.auction_api.dto.enums.UserRole;
import com.example.auction_api.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    List<User> findByRole(UserRole role);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    User save(User user);
    User update(User user);
    void deleteById(Long id);
}
