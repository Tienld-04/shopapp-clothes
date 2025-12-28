package com.example.shop_clothes.dto.vnpay;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VnpayPaymentResponse {

    private String paymentUrl;          // URL để redirect sang VNPAY
    private String transactionCode;     // Mã giao dịch: ORD123456789
    private Long amount;                // Số tiền thanh toán
    private Long orderId;               // Order ID
    private String message;             // Thông báo
}