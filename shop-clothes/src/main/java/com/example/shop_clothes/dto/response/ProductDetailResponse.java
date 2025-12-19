package com.example.shop_clothes.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponse {
//    private Long id;
    private String color;
    private String size;
    private Integer quantity;
    private Float priceAdjustment;
//    private String imageUrl;
}
