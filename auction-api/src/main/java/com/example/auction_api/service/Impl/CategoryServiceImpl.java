package com.example.auction_api.service.Impl;

import com.example.auction_api.dao.CategoryDao;
import com.example.auction_api.dto.request.CategoryRequest;
import com.example.auction_api.dto.response.CategoryResponse;
import com.example.auction_api.entity.Category;
import com.example.auction_api.exception.DuplicateNameException;
import com.example.auction_api.exception.HasAssociationException;
import com.example.auction_api.exception.ResourceNotFoundException;
import com.example.auction_api.mapper.CategoryMapper;
import com.example.auction_api.service.AuctionService;
import com.example.auction_api.service.CategoryService;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDao categoryDao;
    private final CategoryMapper mapper;
    private final AuctionService auctionService;

    public CategoryServiceImpl(CategoryDao categoryDao, CategoryMapper mapper, @Lazy AuctionService auctionService) {
        this.categoryDao = categoryDao;
        this.mapper = mapper;
        this.auctionService = auctionService;
    }

    @Override
    public List<CategoryResponse> getCategories() {
        return categoryDao.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Category.class.getSimpleName(), id));

        return mapper.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest category) {
        validateNameUniqueness(category.name(), null);

        Category newCategory = new Category(category.name());

        return mapper.toResponse(categoryDao.save(newCategory));
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest category) {
        Category existingCategory = getCategoryEntityById(id);
        validateNameUniqueness(category.name(), id);

        existingCategory.setName(category.name());

        return mapper.toResponse(categoryDao.update(existingCategory));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {

        if(!auctionService.getAuctionsByCategory(id).isEmpty()) {
            throw new HasAssociationException(Category.class.getSimpleName(), id);
        }

        //TODO replace categories to delete in auctions

        getCategoryEntityById(id);
        categoryDao.deleteById(id);
    }

    @Override
    public Category getCategoryEntityById(Long id) {
        return categoryDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Category.class.getSimpleName(), id));
    }

    private void validateNameUniqueness(String name, Long categoryId) {
        if(!categoryDao.findByName(name, categoryId).isEmpty()) {
            throw new DuplicateNameException(Category.class.getSimpleName(), name);
        }
    }
}
