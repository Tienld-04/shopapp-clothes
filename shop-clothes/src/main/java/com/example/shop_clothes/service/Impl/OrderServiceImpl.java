package com.example.shop_clothes.service.Impl;

import com.example.shop_clothes.repository.OrderDetailRepository;
import com.example.shop_clothes.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl {
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;


}
