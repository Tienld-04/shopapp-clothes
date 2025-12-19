package com.example.shop_clothes.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private Float price;
    private String material;
    private String description;
    private String brand;

    //
    private String category;

    private List<String> ImagesUrl;
    private List<ProductImageResponse> images;
}
