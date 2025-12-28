package com.example.shop_clothes.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_payment_snapshot")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPaymentSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_detail_id",nullable = false)
    private ProductDetail productDetail;

    @Column(name = "original_quantity")
    private Integer originalQuantity;

    @Column(name = "quantity_reserved")
    private Integer quantityReserved;
}

