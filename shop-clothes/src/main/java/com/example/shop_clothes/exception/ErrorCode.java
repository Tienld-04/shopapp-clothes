package com.example.shop_clothes.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZEO_EXCEPTION(9999, "uncategorized error.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error.", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1002, "Email existed.", HttpStatus.CONFLICT),
    EMAIL_INVALID(1003, "Email is invalid.", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "Password is invalid.", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_EXISTED(1005, "Email not existed.",HttpStatus.NOT_FOUND),

    UNAUTHENTICATED(1006, "unauthenticated.", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "you to not add permission.", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}.", HttpStatus.BAD_REQUEST),

    USER_EXISTED(2001, "User already exists.", HttpStatus.CONFLICT),
    USER_NOT_EXISTED(2002, "User does not exist in the system.", HttpStatus.NOT_FOUND),
    PHONENUMBER_EXISTED(2003, "Phone number already exists.", HttpStatus.CONFLICT),
    PHONE_NUMBER_REQUIRED(2004, "Please update your phone number.", HttpStatus.BAD_REQUEST),

    CATEGORY_EXISTED(3001, "Category already exists.", HttpStatus.CONFLICT),
    SHOPPINGCART_NOT_EXISTED(3002, "Shopping cart already exists.", HttpStatus.NOT_FOUND),
    CART_ITEM_NOT_FOUND(3003, "Không tìm thấy sản phẩm trong giỏ hàng.", HttpStatus.NOT_FOUND),

    ORDER_NOT_EXISTED(4001, "Đơn hàng không tồn tại", HttpStatus.NOT_FOUND),
    ORDER_ROLLBACK_FAILED(4002, "Lỗi hoàn lại đơn hàng", HttpStatus.INTERNAL_SERVER_ERROR),
    ORDER_ADDRESS_REQUIRED(4003, "Vui lòng cập nhật địa chỉ giao hàng", HttpStatus.BAD_REQUEST),

    PRODUCT_DETAIL_NOT_EXISTED(5001, "Chi tiết sản phẩm không tồn tại", HttpStatus.NOT_FOUND),
    INSUFFICIENT_STOCK(5002, "Không đủ hàng trong kho", HttpStatus.CONFLICT),

    VNPAY_PAYMENT_FAILED(6001, "Lỗi tạo thanh toán VNPAY", HttpStatus.BAD_GATEWAY),
    VNPAY_PAYMENT_CONFIG_ERROR(6002, "Cấu hình thanh toán VNPAY không hợp lệ", HttpStatus.INTERNAL_SERVER_ERROR),
    VNPAY_PAYMENT_INVALID_REQUEST(6003, "Dữ liệu thanh toán không hợp lệ", HttpStatus.BAD_REQUEST),

    INVALID_RECEIPT_STATUS(7001, "ReceiptStatus không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_SHIPPING_METHOD(7002, "ShippingMethod không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_STATUS(7003, "OrderStatus không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_METHOD(7004, "PaymentMethod không hợp lệ", HttpStatus.BAD_REQUEST),
    ;
    ErrorCode(int code, String message , HttpStatusCode httpStatusCode) {
        this.message = message;
        this.code = code;
        this.httpStatusCode = httpStatusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;

}
