package com.example.auction_api.dao;

import com.example.auction_api.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryDao {
    List<Category> findAll();
    List<Category> findByName(String name, Long id);
    Optional<Category> findById(Long id);
    Category save(Category category);
    Category update(Category category);
    void deleteById(Long id);
}
