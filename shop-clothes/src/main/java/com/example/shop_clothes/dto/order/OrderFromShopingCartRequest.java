package com.example.shop_clothes.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderFromShopingCartRequest {
    private String note;
    private String shippingMethod;
    private Float shippingFee;
    private String paymentMethod;

    private List<Long> cartItemIds;
}
