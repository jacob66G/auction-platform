package com.example.auction_api.service;

import com.example.auction_api.dto.request.CategoryRequest;
import com.example.auction_api.dto.response.CategoryResponse;
import com.example.auction_api.entity.Category;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getCategories();
    CategoryResponse getCategoryById(Long id);
    CategoryResponse createCategory(CategoryRequest category);
    CategoryResponse updateCategory(Long id, CategoryRequest category);
    Category getCategoryEntityById(Long id);
    void deleteCategory(Long id);
}
