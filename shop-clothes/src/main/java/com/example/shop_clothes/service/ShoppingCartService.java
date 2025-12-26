package com.example.shop_clothes.service;

import com.example.shop_clothes.dto.shopingcart.CartItemRequest;

public interface ShoppingCartService {
    String addItemToShoppingCart(CartItemRequest cartItemRequest);
}
