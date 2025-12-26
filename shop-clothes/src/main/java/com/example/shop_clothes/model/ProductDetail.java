package com.example.shop_clothes.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_details", uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "color", "size"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetail extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "size", length = 20)
    private String size;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;

    @Column(name = "price_adjustment")
    private Float priceAdjustment = 0f;
    // delete
//    @Column(name = "image_url", length = 300)
//    private String imageUrl;

    @Column(name = "is_active")
    private Boolean isActive = true;

}