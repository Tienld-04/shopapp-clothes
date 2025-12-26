package com.example.shop_clothes.dto.shopingcart;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemRequest {
    private Long productId;
    private Long productDetailId;
    private Integer quantity;
}
