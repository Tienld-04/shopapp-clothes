package com.example.shop_clothes.service;

import com.example.shop_clothes.dto.record.ShippingMethodResponse;

import java.util.List;

public interface ShippingService {
    List<ShippingMethodResponse> listShippingMethod();
}
