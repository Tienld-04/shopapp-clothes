package com.example.shop_clothes.controller;

import com.example.shop_clothes.dto.order.OrderFromShopingCartRequest;
import com.example.shop_clothes.dto.order.OrderResponse;
import com.example.shop_clothes.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> Order(
            @RequestBody OrderFromShopingCartRequest orderFromShopingCartRequest,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(orderService.orderFromShopingcart(orderFromShopingCartRequest, httpRequest));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }
}
