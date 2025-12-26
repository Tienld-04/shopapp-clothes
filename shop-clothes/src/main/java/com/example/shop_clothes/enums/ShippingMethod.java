package com.example.shop_clothes.enums;

import lombok.Getter;

@Getter
public enum ShippingMethod {
    STANDARD("Giao hàng tiêu chuẩn", 30000f),
    EXPRESS("Giao hàng nhanh", 50000f);

    private final String displayName;
    private final Float fee;

    ShippingMethod(String displayName, Float fee) {
        this.displayName = displayName;
        this.fee = fee;
    }
    public static ShippingMethod fromDisplayName(String value) {
        for (ShippingMethod method : values()) {
            if (method.displayName.equalsIgnoreCase(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("ShippingMethod không hợp lệ: " + value);
    }

}
