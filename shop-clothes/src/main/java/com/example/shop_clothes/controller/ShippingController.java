package com.example.shop_clothes.controller;

import com.example.shop_clothes.dto.record.ShippingMethodResponse;
import com.example.shop_clothes.service.ShippingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/shipping")
@RequiredArgsConstructor
public class ShippingController {
    private final ShippingService shippingService;

    @GetMapping
    public ResponseEntity<List<ShippingMethodResponse>> getAllShippingMethod(){
        return ResponseEntity.ok(shippingService.listShippingMethod());
    }
}
