package com.example.shop_clothes.repository;

import com.example.shop_clothes.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
}
