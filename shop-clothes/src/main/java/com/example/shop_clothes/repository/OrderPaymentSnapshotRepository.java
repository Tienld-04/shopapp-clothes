package com.example.shop_clothes.repository;

import com.example.shop_clothes.model.OrderPaymentSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderPaymentSnapshotRepository extends JpaRepository<OrderPaymentSnapshot,Long> {
    List<OrderPaymentSnapshot> findByOrderId(Long orderId);
}
