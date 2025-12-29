package com.example.shop_clothes.enums;

import com.example.shop_clothes.exception.ApplicationException;
import com.example.shop_clothes.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("Đã đặt hàng, chưa thanh toán"),
    PAYMENT_WAITING("Đang chờ thanh toán (Đã đặt hàng)"),
    PAID("Đã thanh toán"),
    PAYMENT_FAILED("Thanh toán thất bại"),
    CANCELLED_TIMEOUT("Hủy do quá hết hạn thanh toán"),
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
        throw new ApplicationException(ErrorCode.INVALID_ORDER_STATUS);
    }
}
