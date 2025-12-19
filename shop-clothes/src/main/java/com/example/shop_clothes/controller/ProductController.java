package com.example.shop_clothes.controller;

import com.example.shop_clothes.dto.request.ProductDetailRequest;
import com.example.shop_clothes.dto.request.ProductRequest;
import com.example.shop_clothes.dto.response.CategoryResponse;
import com.example.shop_clothes.dto.response.ProductDetailResponse;
import com.example.shop_clothes.dto.response.ProductResponse;
import com.example.shop_clothes.model.Product;
import com.example.shop_clothes.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createProduct(@ModelAttribute ProductRequest productRequest) throws IOException {
        productService.createProduct(productRequest);
        return ResponseEntity.ok("Thêm sản phẩm thành công");
    }

    @PutMapping(value = "/{product_id}",  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateProduct(
            @PathVariable Long product_id,
            @ModelAttribute ProductRequest productRequest) throws IOException {
        productService.updateProduct(product_id, productRequest);
        return ResponseEntity.ok("Cập nhật sản phẩm thành công");
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{product_id}/details")
    public ResponseEntity<List<ProductDetailResponse>> getAllProductDetailByProductId(
            @PathVariable Long product_id) {
        return ResponseEntity.ok(productService.getAllProductDetailsByProductId(product_id));
    }

    @PostMapping("/{product_id}/details")
    public ResponseEntity<String> createProductDetailByProductId(
            @PathVariable Long product_id,
            @RequestBody ProductDetailRequest productDetailRequest) {
        productService.createProductDetailByProduct(product_id, productDetailRequest);
        return ResponseEntity.ok("Thêm chi tiết sản phẩm thành công ");
    }
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(productService.getAllCategories());
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<String> deleteProductImage(@PathVariable Long imageId) {
        productService.deleteProductImage(imageId);
        return ResponseEntity.ok("Xóa ảnh thành công");
    }

}
