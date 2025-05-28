package com.example.auction_api.service.Impl;

import com.example.auction_api.dao.UserDao;
import com.example.auction_api.dto.enums.UserRole;
import com.example.auction_api.dto.request.ChangePasswordRequest;
import com.example.auction_api.dto.request.DepositRequest;
import com.example.auction_api.dto.request.UserRegisterRequest;
import com.example.auction_api.dto.request.UserRequest;
import com.example.auction_api.dto.response.UserResponse;
import com.example.auction_api.entity.User;
import com.example.auction_api.exception.EmailAlreadyExistsException;
import com.example.auction_api.exception.InsufficientAmountException;
import com.example.auction_api.exception.InvalidPasswordException;
import com.example.auction_api.exception.UsernameAlreadyExistsException;
import com.example.auction_api.mapper.UserMapper;
import com.example.auction_api.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper mapper;
    private final AuthenticationServiceImpl authService;

    public UserServiceImpl(
            UserDao userDao,
            BCryptPasswordEncoder passwordEncoder,
            UserMapper mapper,
            AuthenticationServiceImpl authService
    ) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
        this.authService = authService;
    }

    @Override
    @Transactional
    public UserResponse registerUser(UserRegisterRequest user) {
        validateUsername(user.username());
        validateUserEmail(user.email());

        User newUser = new User();
        newUser.setUsername(user.username());
        newUser.setEmail(user.email());
        newUser.setPassword(passwordEncoder.encode(user.password()));
        newUser.setBalance(BigDecimal.ZERO);
        newUser.setRole(UserRole.USER);

        return mapper.toResponse(userDao.save(newUser));
    }

    @Override
    @Transactional
    public UserResponse updateUser(UserRequest user) {
        User existingUser = authService.getAuthenticatedUser();

        validateUsername(user.username());
        validateUserEmail(user.email());

        existingUser.setUsername(user.username());
        existingUser.setEmail(user.email());


        return mapper.toResponse(userDao.update(existingUser));
    }

    @Override
    @Transactional
    public void refund(BigDecimal amount, User user) {
        user.setBalance(user.getBalance().add(amount));
        userDao.update(user);
    }

    @Override
    @Transactional
    public void decreaseBalance(BigDecimal amount, User user) {
        if (user.getBalance() == null || user.getBalance().compareTo(amount) < 0) {
            throw new InsufficientAmountException();
        }

        user.setBalance(user.getBalance().subtract(amount));
        userDao.update(user);
    }

    @Override
    @Transactional
    public void deposit(DepositRequest request) {
        User user = authService.getAuthenticatedUser();
        BigDecimal userBalance = user.getBalance() == null ? BigDecimal.ZERO : user.getBalance();

        user.setBalance(userBalance.add(request.amount()));
        userDao.update(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userDao.deleteById(id);
    }

    @Override
    public UserResponse getUser() {
        User user = authService.getAuthenticatedUser();
        return mapper.toResponse(user);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = authService.getAuthenticatedUser();

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Old password is invalid");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userDao.update(user);
    }

    private void validateUsername(String username) {
        Optional<User> user = userDao.findByUsername(username);

        if (user.isPresent() && !user.get().getId().equals(authService.getAuthenticatedUser().getId())) {
            throw new UsernameAlreadyExistsException(username);
        }
    }

    private void validateUserEmail(String email) {
        Optional<User> user = userDao.findByEmail(email);

        if (user.isPresent() && !user.get().getId().equals(authService.getAuthenticatedUser().getId())) {
            throw new EmailAlreadyExistsException(email);
        }
    }
}
