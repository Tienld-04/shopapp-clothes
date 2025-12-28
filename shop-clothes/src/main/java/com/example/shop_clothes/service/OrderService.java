package com.example.shop_clothes.service;

import com.example.shop_clothes.dto.order.OrderFromShopingCartRequest;
import com.example.shop_clothes.dto.order.OrderResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface OrderService {
    OrderResponse orderFromShopingcart(OrderFromShopingCartRequest orderFromShopingCartRequest, HttpServletRequest httpRequest);
    OrderResponse getOrderById(Long id);
//    OrderResponse OrderFromShopingcart(OrderFromShopingCartRequest orderFromShopingCartRequest);
}
