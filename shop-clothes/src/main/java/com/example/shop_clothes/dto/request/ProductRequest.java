package com.example.shop_clothes.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ProductRequest {
    private String name;
    private Float price;
    private String thumbnail;
    private String description;
    private String material;
    private String brand;
//    private Boolean isActive;

    private Long categoryId;
    //private List<ProductDetailRequest> productDetails;
    private List<MultipartFile> images;

}
