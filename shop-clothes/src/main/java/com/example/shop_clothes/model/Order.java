package com.example.shop_clothes.model;

import com.example.shop_clothes.enums.OrderStatus;
import com.example.shop_clothes.enums.PaymentMethod;
import com.example.shop_clothes.enums.ShippingMethod;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends  BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "fullname", length = 100)
    private String fullname;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "address", nullable = false, length = 200)
    private String address;

    @Column(name = "note", length = 100)
    private String note;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "total_money")
    private Float totalMoney;

    @Enumerated(EnumType.STRING)
    @Column(name = "shipping_method", nullable = false)
    private ShippingMethod shippingMethod;

    @Column(name = "shipping_address", length = 200)
    private String shippingAddress;

    @Column(name = "shipping_date")
    private LocalDate shippingDate;

    @Column(name = "shipping_fee")
    private Float shippingFee = 0f;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "active")
    private Boolean active = true;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;

//    public enum OrderStatus {
//        pending, paid, processing, shipped, delivered, cancelled
//    }
}
