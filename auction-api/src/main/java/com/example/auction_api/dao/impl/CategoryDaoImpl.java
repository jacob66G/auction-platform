package com.example.auction_api.dao.impl;

import com.example.auction_api.dao.CategoryDao;
import com.example.auction_api.entity.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryDaoImpl implements CategoryDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Category> findAll() {
        return em.createQuery("SELECT c FROM Category c", Category.class)
                .getResultList();
    }

    @Override
    public List<Category> findByName(String name, Long id) {
        String query = "SELECT c FROM Category c WHERE LOWER(c.name) = LOWER(:name) AND (:id IS NULL OR c.id != :id)";

        return em.createQuery(query, Category.class)
                .setParameter("name", name)
                .setParameter("id", id)
                .getResultList();
    }

    @Override
    public Optional<Category> findById(Long id) {
        return Optional.ofNullable(em.find(Category.class, id));
    }

    @Override
    public Category save(Category category) {
        em.persist(category);
        return category;
    }

    @Override
    public Category update(Category category) {
        return em.merge(category);
    }

    @Override
    public void deleteById(Long id) {
        em.remove(em.find(Category.class, id));
    }
}
