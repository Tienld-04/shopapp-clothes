package com.example.shop_clothes.service.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.shop_clothes.dto.request.ProductDetailRequest;
import com.example.shop_clothes.dto.request.ProductRequest;
import com.example.shop_clothes.dto.response.CategoryResponse;
import com.example.shop_clothes.dto.response.ProductDetailResponse;
import com.example.shop_clothes.dto.response.ProductResponse;
import com.example.shop_clothes.model.Category;
import com.example.shop_clothes.model.Product;
import com.example.shop_clothes.model.ProductDetail;
import com.example.shop_clothes.model.ProductImage;
import com.example.shop_clothes.repository.CategoryRepository;
import com.example.shop_clothes.repository.ProductDetailRepository;
import com.example.shop_clothes.repository.ProductImageRepository;
import com.example.shop_clothes.repository.ProductRepository;
import com.example.shop_clothes.service.ProductService;
//import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductDetailRepository productDetailRepository;
    private final Cloudinary cloudinary;

    public Map uploadFile(MultipartFile file, String folder) throws IOException {
        Map<String, Object> options = ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "auto"
        );
        return cloudinary.uploader().upload(file.getBytes(), options);
    }

    @Override
    @Transactional
    public Product createProduct(ProductRequest productRequest) throws IOException {
        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setBrand(productRequest.getBrand());
        product.setPrice(productRequest.getPrice());
        product.setMaterial(productRequest.getMaterial());
        product.setDescription(productRequest.getDescription());
        product.setThumbnail(productRequest.getThumbnail());
        product.setCategory(category);
        product = productRepository.save(product);

        List<MultipartFile> images = productRequest.getImages();
        List<ProductImage> productImageList = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile img : images) {
                Map up = uploadFile(img, "diary_images");
                String url = (String) up.get("secure_url");
                ProductImage productImage = new ProductImage();
                productImage.setImageUrl(url);
                productImage.setProduct(product);
                productImageList.add(productImage);
            }
        }
        product.setImages(productImageList);
        productImageRepository.saveAll(productImageList);
        return product;
    }

    @Override
    @Transactional
    public Product updateProduct(Long productId, ProductRequest productRequest) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        product.setName(productRequest.getName());
        product.setBrand(productRequest.getBrand());
        product.setPrice(productRequest.getPrice());
        product.setMaterial(productRequest.getMaterial());
        product.setDescription(productRequest.getDescription());
        product.setThumbnail(productRequest.getThumbnail());
        product.setCategory(category);
        product = productRepository.save(product);

        List<MultipartFile> images = productRequest.getImages();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile img : images) {
                Map up = uploadFile(img, "diary_images");
                String url = (String) up.get("secure_url");
                ProductImage productImage = new ProductImage();
                productImage.setImageUrl(url);
                productImage.setProduct(product);
                productImageRepository.save(productImage);
            }
        }
        return product;
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductResponse> productResponseList = new ArrayList<>();
        for (Product product : products) {
            ProductResponse productResponse = new ProductResponse();
            productResponse.setId(product.getId());
            productResponse.setName(product.getName());
            productResponse.setBrand(product.getBrand());
            productResponse.setPrice(product.getPrice());
            productResponse.setMaterial(product.getMaterial());
            productResponse.setDescription(product.getDescription());
            productResponse.setCategory(product.getCategory().getName());
            productResponse.setImagesUrl(product.getImages().stream().map(ProductImage::getImageUrl).collect(Collectors.toList()));
            productResponse.setImages(product.getImages().stream().map(img -> new com.example.shop_clothes.dto.response.ProductImageResponse(
                    img.getId(),
                    img.getImageUrl(),
                    img.getIsPrimary(),
                    img.getDisplayOrder()
            )).collect(Collectors.toList()));
            productResponseList.add(productResponse);
        }
        return productResponseList;
    }

    @Override
    public List<ProductDetailResponse> getAllProductDetailsByProductId(Long productId) {
        List<ProductDetail> productDetails = productDetailRepository.findByProduct_Id(productId);
        List<ProductDetailResponse> productDetailResponseList = new ArrayList<>();
        for (ProductDetail productDetail : productDetails) {
            ProductDetailResponse productDetailResponse = new ProductDetailResponse();
            productDetailResponse.setColor(productDetail.getColor());
            productDetailResponse.setSize(productDetail.getSize());
            productDetailResponse.setQuantity(productDetail.getQuantity());
            productDetailResponse.setPriceAdjustment(productDetail.getPriceAdjustment());
            productDetailResponseList.add(productDetailResponse);
        }
        return productDetailResponseList;
    }

    @Override
    @Transactional
    public ProductDetail createProductDetailByProduct(Long productId, ProductDetailRequest productDetailRequest) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("product not found"));
        ProductDetail productDetail = new ProductDetail();
        productDetail.setColor(productDetailRequest.getColor());
        productDetail.setSize(productDetailRequest.getSize());
        productDetail.setQuantity(productDetailRequest.getQuantity());
        productDetail.setPriceAdjustment(productDetailRequest.getPriceAdjustment());
        productDetail.setProduct(product);

        return productDetailRepository.save(productDetail);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponse> categoryResponseList = new ArrayList<>();
        for (Category category : categories) {
            CategoryResponse categoryResponse = new CategoryResponse();
            categoryResponse.setId(category.getId());
            categoryResponse.setName(category.getName());
            categoryResponse.setDescription(category.getDescription());
            categoryResponseList.add(categoryResponse);
        }
        return categoryResponseList;
    }

    @Override
    @Transactional
    public void deleteProductImage(Long imageId) {
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("product image not found"));
        productImage.setProduct(null);
        productImageRepository.save(productImage);
        productImageRepository.deleteById(imageId);
    }

}
