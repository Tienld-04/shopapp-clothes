package com.example.shop_clothes.enums;

import com.example.shop_clothes.exception.ApplicationException;
import com.example.shop_clothes.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum ReceiptStatus {
    PENDING("Chờ xác nhận"),
    CONFIRMED("Đã xác nhận"),
    CANCELLED("Đã hủy");
    private final String displayName;
    ReceiptStatus(String displayName) {
        this.displayName = displayName;
    }
    public static ReceiptStatus fromDisplayName(String value) {
        for (ReceiptStatus status : values()) {
            if (status.displayName.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new ApplicationException(ErrorCode.INVALID_RECEIPT_STATUS);
    }
}
