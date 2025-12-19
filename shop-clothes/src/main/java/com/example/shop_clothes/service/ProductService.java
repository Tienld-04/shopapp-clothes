package com.example.shop_clothes.service;

import com.example.shop_clothes.dto.request.ProductDetailRequest;
import com.example.shop_clothes.dto.request.ProductRequest;
import com.example.shop_clothes.dto.response.CategoryResponse;
import com.example.shop_clothes.dto.response.ProductDetailResponse;
import com.example.shop_clothes.dto.response.ProductResponse;
import com.example.shop_clothes.model.Product;
import com.example.shop_clothes.model.ProductDetail;

import java.io.IOException;
import java.util.List;
//@Service
public interface ProductService {
    Product createProduct(ProductRequest productRequest) throws IOException;
    Product updateProduct(Long productId, ProductRequest productRequest) throws IOException;
    List<ProductResponse> getAllProducts();

    List<ProductDetailResponse> getAllProductDetailsByProductId(Long productId);

    ProductDetail createProductDetailByProduct(Long productId, ProductDetailRequest productDetailRequest);
    List<CategoryResponse> getAllCategories();
    void deleteProductImage(Long imageId);
}
