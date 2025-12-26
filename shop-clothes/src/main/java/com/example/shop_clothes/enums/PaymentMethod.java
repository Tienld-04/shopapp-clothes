package com.example.shop_clothes.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    COD("Thanh toán khi nhận hàng"),
    MOMO("Ví điện tử Momo"),
    VNPAY("Cổng thanh toán VNPay");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public static PaymentMethod fromDisplayName(String value) {
        for (PaymentMethod method : values()) {
            if (method.displayName.equalsIgnoreCase(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("PaymentMethod không hợp lệ: " + value);
    }
}
