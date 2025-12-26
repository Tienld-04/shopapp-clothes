package com.example.shop_clothes.service.Impl;

import com.example.shop_clothes.enums.PaymentMethod;
import com.example.shop_clothes.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    public List<String> listPaymentMethod() {
        return Arrays.stream(PaymentMethod.values())
                .map(PaymentMethod::getDisplayName)
                .toList();
    }
}
