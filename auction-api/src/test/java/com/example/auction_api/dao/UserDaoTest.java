package com.example.auction_api.dao;

import com.example.auction_api.dao.impl.UserDaoImpl;
import com.example.auction_api.dto.enums.UserRole;
import com.example.auction_api.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(UserDaoImpl.class)
@ActiveProfiles("test")
class UserDaoTest {

    @Autowired
    private UserDao userDao;

    @Test
    void findByRole_ShouldReturnUsersWithUserRole() {
        // given
        UserRole userRole = UserRole.USER;

        // when
        List<User> result = userDao.findByRole(userRole);

        // then
        assertThat(result).hasSize(4);
        assertThat(result).extracting(User::getUsername)
                .containsExactlyInAnyOrder("dave", "alice", "bob", "eve");
        assertThat(result).allMatch(user -> user.getRole() == UserRole.USER);
    }

    @Test
    void findByRole_ShouldReturnUsersWithAdminRole() {
        // given
        UserRole adminRole = UserRole.ADMIN;

        // when
        List<User> result = userDao.findByRole(adminRole);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("grace");
        assertThat(result.get(0).getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void findByRole_ShouldReturnUsersWithModeratorRole() {
        // given
        UserRole moderatorRole = UserRole.MODERATOR;

        // when
        List<User> result = userDao.findByRole(moderatorRole);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getUsername)
                .containsExactlyInAnyOrder("carol", "frank");
        assertThat(result).allMatch(user -> user.getRole() == UserRole.MODERATOR);
    }


    @Test
    void findById_ShouldReturnUser_WhenExists() {
        // given
        Long userId = 1L; // grace

        // when
        Optional<User> result = userDao.findById(userId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("grace");
        assertThat(result.get().getEmail()).isEqualTo("grace@example.com");
        assertThat(result.get().getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        // given
        Long nonExistentId = 999L;

        // when
        Optional<User> result = userDao.findById(nonExistentId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenExists() {
        // given
        String username = "alice";

        // when
        Optional<User> result = userDao.findByUsername(username);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("alice");
        assertThat(result.get().getEmail()).isEqualTo("alice@example.com");
        assertThat(result.get().getBalance()).isEqualByComparingTo(BigDecimal.valueOf(500.00));
    }

    @Test
    void findByUsername_ShouldReturnEmpty_WhenNotExists() {
        // given
        String nonExistentUsername = "nonexistent";

        // when
        Optional<User> result = userDao.findByUsername(nonExistentUsername);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenExists() {
        // given
        String email = "bob@example.com";

        // when
        Optional<User> result = userDao.findByEmail(email);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("bob");
        assertThat(result.get().getEmail()).isEqualTo("bob@example.com");
        assertThat(result.get().getBalance()).isEqualByComparingTo(BigDecimal.valueOf(800.00));
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenNotExists() {
        // given
        String nonExistentEmail = "nonexistent@example.com";

        // when
        Optional<User> result = userDao.findByEmail(nonExistentEmail);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void save_ShouldPersistUser() {
        // given
        User user = new User();
        user.setUsername("john_doe");
        user.setEmail("john@example.com");
        user.setPassword("password");
        user.setBalance(BigDecimal.valueOf(100));
        user.setRole(UserRole.USER);

        // when
        User saved = userDao.save(user);

        // then
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void update_ShouldUpdateUserFields() {
        // given
        User user = userDao.findById(1L).get();
        user.setUsername("test");
        user.setEmail("test@example.com");

        // when
        User updated = userDao.update(user);

        // then
        assertThat(updated.getUsername()).isEqualTo("test");
        assertThat(updated.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void deleteById_ShouldRemoveUser() {
        // when
        userDao.deleteById(1L);

        // then
        Optional<User> result = userDao.findById(1L);
        assertThat(result).isEmpty();
    }
}