package com.example.shop_clothes.service;

import com.example.shop_clothes.dto.request.CategoryRequest;
import com.example.shop_clothes.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest categoryRequest);
    List<CategoryResponse> getAllCategories();

}
