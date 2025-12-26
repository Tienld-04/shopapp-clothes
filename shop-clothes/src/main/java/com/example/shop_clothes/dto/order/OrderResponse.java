package com.example.shop_clothes.dto.order;

import com.example.shop_clothes.enums.OrderStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private String fullname;
    private String phoneNumber;
    private String deliveryAddress;
    private Long orderId;

    private Float shippingFee;
    private Float totalMoney;
    private OrderStatus status;
    private String message;
    private List<OrderItemDetailResponse> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemDetailResponse{
        private String productName;
        private String color;
        private String size;
        private Integer quantity;
        private Float price;
        private Float totalMoney;
    }
}
