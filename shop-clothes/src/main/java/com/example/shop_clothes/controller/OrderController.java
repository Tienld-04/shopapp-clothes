package com.example.shop_clothes.controller;

import com.example.shop_clothes.dto.order.OrderFromShopingCartRequest;
import com.example.shop_clothes.dto.order.OrderResponse;
import com.example.shop_clothes.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> Order(@RequestBody OrderFromShopingCartRequest orderFromShopingCartRequest){
        return ResponseEntity.ok(orderService.OrderFromShopingcart(orderFromShopingCartRequest));
    }
}
