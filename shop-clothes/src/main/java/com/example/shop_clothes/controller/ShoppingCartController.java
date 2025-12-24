package com.example.shop_clothes.controller;

import com.example.shop_clothes.dto.ShoppingCart.CartItemRequest;
import com.example.shop_clothes.model.ShoppingCart;
import com.example.shop_clothes.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PostMapping
    public ResponseEntity<String> addToCart(@RequestBody CartItemRequest cartItemRequest) {
        return ResponseEntity.ok(shoppingCartService.addItemToShoppingCart(cartItemRequest));
    }

}
