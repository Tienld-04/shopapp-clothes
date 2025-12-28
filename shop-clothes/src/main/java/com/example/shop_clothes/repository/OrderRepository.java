package com.example.shop_clothes.repository;

import com.example.shop_clothes.enums.OrderStatus;
import com.example.shop_clothes.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.paymentExpireTime < :now")
    List<Order> findExpiredOrders(@Param("status") OrderStatus status, @Param("now") LocalDateTime now);
}
