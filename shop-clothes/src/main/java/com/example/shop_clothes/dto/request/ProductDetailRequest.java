package com.example.shop_clothes.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDetailRequest {
    private String color;
    private String size;
    private Integer quantity;
    private Float priceAdjustment;
   // private String imageUrl;
    //private Boolean isActive;
}
