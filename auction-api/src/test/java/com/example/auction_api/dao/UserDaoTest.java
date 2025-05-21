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
    void findById_ShouldReturnUser_WhenExists() {
        // when
        Optional<User> result = userDao.findById(1L);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("grace");
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenExists() {
        // when
        Optional<User> result = userDao.findByUsername("grace");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("grace@example.com");
    }

    @Test
    void getByEmail_ShouldReturnUser_WhenExists() {
        // when
        Optional<User> result = userDao.getByEmail("alice@example.com");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("alice");
    }

    @Test
    void findByRole_ShouldReturnUsersWithGivenRole() {
        //given
        User admin1 = userDao.findById(1L).get();
        User admin2 = userDao.findById(2L).get();

        List<User> expectedUsers = Arrays.asList(admin1, admin2);

        // when
        List<User> result = userDao.findByRole(UserRole.ADMIN);

        // then
        assertThat(result).hasSize(2);
        assertEquals(expectedUsers, result);
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