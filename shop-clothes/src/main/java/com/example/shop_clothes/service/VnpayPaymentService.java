package com.example.shop_clothes.service;

import com.example.shop_clothes.dto.vnpay.VnpayCallbackResponse;
import com.example.shop_clothes.dto.vnpay.VnpayPaymentResponse;
import com.example.shop_clothes.model.Order;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface VnpayPaymentService {
    VnpayPaymentResponse createPaymentUrl(Order order, HttpServletRequest request);
    String getCallbackInfo(Map<String, String> params);
    boolean verifyCallback(Map<String, String> params);
    //
    VnpayCallbackResponse handleCallback(Map<String, String> params);
}
