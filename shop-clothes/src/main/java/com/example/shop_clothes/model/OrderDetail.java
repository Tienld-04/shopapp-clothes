package com.example.shop_clothes.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetail extends  BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "product_detail_id")
    private ProductDetail productDetail;

    @Column(name = "price")
    private Float price;

    @Column(name = "number_of_products")
    private Integer numberOfProducts;

    @Column(name = "total_money")
    private Float totalMoney;

    @Column(name = "color", length = 20)
    private String color;

    @Column(name = "size", length = 20)
    private String size;
}