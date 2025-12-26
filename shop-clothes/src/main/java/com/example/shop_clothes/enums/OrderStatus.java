package com.example.shop_clothes.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("Đã đặt hàng, chưa thanh toán"),
    PAID("Đã thanh toán"),
    PROCESSING("Đang xử lý đơn hàng"),
    SHIPPED("Đã giao cho đơn vị vận chuyển"),
    DELIVERED("Đã giao thành công"),
    CANCELLED("Đơn hàng đã bị hủy");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }
    public static OrderStatus fromDisplayName(String value) {
        for (OrderStatus method : values()) {
            if (method.displayName.equalsIgnoreCase(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("OrderStatus không hợp lệ: " + value);
    }
}
