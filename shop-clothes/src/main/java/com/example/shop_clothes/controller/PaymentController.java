package com.example.shop_clothes.controller;

import com.example.shop_clothes.dto.vnpay.VnpayCallbackResponse;
import com.example.shop_clothes.service.PaymentService;
import com.example.shop_clothes.service.VnpayPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;
    private final VnpayPaymentService vnpayPaymentService;

    @GetMapping
    public ResponseEntity<List<String>> getAllPaymentMethod() {
        return ResponseEntity.ok(paymentService.listPaymentMethod());
    }
    @GetMapping("/vnpay/return")
    public RedirectView vnpayCallback(@RequestParam Map<String, String> params) {
        VnpayCallbackResponse response = vnpayPaymentService.handleCallback(params);
        return new RedirectView(response.getRedirectUrl());
    }
}
