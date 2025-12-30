package com.example.shop_clothes.repository;

import com.example.shop_clothes.model.StockReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockReceiptRepository extends JpaRepository<StockReceipt, Long> {
}
