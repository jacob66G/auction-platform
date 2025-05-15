package com.example.auction_api.dao.impl;

import com.example.auction_api.dao.UserDao;
import com.example.auction_api.dto.enums.UserRole;
import com.example.auction_api.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<User> findByRole(UserRole role) {
        String query = "SELECT u FROM User u WHERE u.role = :role";

        return em.createQuery(query, User.class)
                .setParameter("role", role)
                .getResultList();
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String query = "SELECT u FROM User u WHERE u.username = :username";

        return em.createQuery(query, User.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    @Override
    public Optional<User> getByEmail(String email) {
        String query = "SELECT u FROM User u WHERE u.email = :email";

        return em.createQuery(query, User.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }

    @Override
    public User save(User user) {
        em.persist(user);
        return user;
    }

    @Override
    public User update(User user) {
        return em.merge(user);
    }

    @Override
    public void deleteById(Long id) {
        em.remove(em.find(User.class, id));
    }

}
