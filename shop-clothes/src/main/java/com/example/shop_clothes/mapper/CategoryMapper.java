package com.example.shop_clothes.mapper;

import com.example.shop_clothes.dto.request.CategoryRequest;
import com.example.shop_clothes.dto.response.CategoryResponse;
import com.example.shop_clothes.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryRequest categoryRequest);
    CategoryResponse toCategoryResponse(Category category);
}
