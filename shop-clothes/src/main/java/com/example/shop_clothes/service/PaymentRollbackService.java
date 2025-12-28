package com.example.shop_clothes.service;

import com.example.shop_clothes.model.Order;

public interface PaymentRollbackService {
    void rollbackPayment(Order order, String reason);
    void cancelOrderByTimeout(Order order);
}
