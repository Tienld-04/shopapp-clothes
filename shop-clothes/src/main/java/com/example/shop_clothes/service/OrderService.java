package com.example.shop_clothes.service;

import com.example.shop_clothes.dto.order.OrderFromShopingCartRequest;
import com.example.shop_clothes.dto.order.OrderResponse;

public interface OrderService {
    OrderResponse OrderFromShopingcart(OrderFromShopingCartRequest orderFromShopingCartRequest);
}
