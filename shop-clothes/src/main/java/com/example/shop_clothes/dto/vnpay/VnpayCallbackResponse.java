package com.example.shop_clothes.dto.vnpay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VnpayCallbackResponse {
    private boolean success;
    private String redirectUrl;
    private String errorMessage;
    private String orderId;
}
