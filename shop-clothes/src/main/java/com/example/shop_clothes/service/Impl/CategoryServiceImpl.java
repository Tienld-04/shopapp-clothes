package com.example.shop_clothes.service.Impl;

import com.example.shop_clothes.dto.request.CategoryRequest;
import com.example.shop_clothes.dto.response.CategoryResponse;
import com.example.shop_clothes.exception.ApplicationException;
import com.example.shop_clothes.exception.ErrorCode;
import com.example.shop_clothes.mapper.CategoryMapper;
import com.example.shop_clothes.model.Category;
import com.example.shop_clothes.repository.CategoryRepository;
import com.example.shop_clothes.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    @Override
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        if(categoryRepository.existsByName(categoryRequest.getName())){
            throw new ApplicationException(ErrorCode.CATEGORY_EXISTED);
        }
        Category category = categoryMapper.toCategory(categoryRequest);
        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }
    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponse> categoryResponseList = new ArrayList<>();
        for (Category category : categories) {
            CategoryResponse categoryResponse = categoryMapper.toCategoryResponse(category);
            categoryResponseList.add(categoryResponse);
        }
        return categoryResponseList;
    }
}
