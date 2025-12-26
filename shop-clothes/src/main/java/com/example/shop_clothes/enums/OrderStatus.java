package com.example.shop_clothes.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {

    PENDING("pending", "Đã đặt hàng, chưa thanh toán"),
    PAID("paid", "Đã thanh toán"),
    PROCESSING("processing", "Đang xử lý đơn hàng"),
    SHIPPED("shipped", "Đã giao cho đơn vị vận chuyển"),
    DELIVERED("delivered", "Đã giao thành công"),
    CANCELLED("cancelled", "Đơn hàng đã bị hủy");

    private final String code;
    private final String description;
}
