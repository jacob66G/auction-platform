package com.example.auction_api.service.Impl;

import com.example.auction_api.dao.UserDao;
import com.example.auction_api.dto.response.UserDetailsResponse;
import com.example.auction_api.entity.Auction;
import com.example.auction_api.enums.UserRole;
import com.example.auction_api.dto.request.ChangePasswordRequest;
import com.example.auction_api.dto.request.DepositRequest;
import com.example.auction_api.dto.request.UserRegisterRequest;
import com.example.auction_api.dto.request.ChangeEmailRequest;
import com.example.auction_api.dto.response.UserResponse;
import com.example.auction_api.entity.User;
import com.example.auction_api.event.auction.AuctionRefundEvent;
import com.example.auction_api.exception.*;
import com.example.auction_api.mapper.UserMapper;
import com.example.auction_api.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
        newUser.setEmailLastChanged(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));

        return mapper.toResponse(userDao.save(newUser));
    }

    @Override
    @Transactional
    public void changeEmail(ChangeEmailRequest request) {
        User existingUser = authService.getAuthenticatedUser();

        validateEmailChangeAllowed(existingUser.getEmailLastChanged());
        validateUserEmail(request.email());

        existingUser.setEmail(request.email());
        existingUser.setEmailLastChanged(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));

        mapper.toResponse(userDao.update(existingUser));
    }


    @Override
    public List<UserResponse> getUsersByRole(String role) {
        return userDao.findByRole(role).stream().map(mapper::toResponse).toList();
    }

    @Override
    public List<UserResponse> getUsersByUsernameOrEmail(String usernameOrEmail) {
        return userDao.findByUsernameOrEmail(usernameOrEmail).stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    public UserResponse assignModeratorRole(Long userId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(User.class.getSimpleName(), userId));

        if (user.getRole() != UserRole.MODERATOR) {
            user.setRole(UserRole.MODERATOR);
            user = userDao.update(user);
        }

        return mapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse revokeModeratorRole(Long userId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(User.class.getSimpleName(), userId));

        if (user.getRole() != UserRole.USER) {
            user.setRole(UserRole.USER);
            user = userDao.update(user);
        }

        return mapper.toResponse(user);
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
    public UserDetailsResponse getUser() {
        User user = authService.getAuthenticatedUser();
        return mapper.toDetailsResponse(user);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = authService.getAuthenticatedUser();

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
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

    private void validateEmailChangeAllowed(LocalDateTime emailLastChanged) {

        if(emailLastChanged.plusDays(30).isAfter(LocalDateTime.now())) {
            throw new EmailChangeNotAllowedException("You can only change your email once every 30 days.");
        }
    }
}
