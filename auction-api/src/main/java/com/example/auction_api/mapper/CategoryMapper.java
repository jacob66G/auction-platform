package com.example.auction_api.mapper;

import com.example.auction_api.dto.response.CategoryResponse;
import com.example.auction_api.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName()
        );
    }
}
