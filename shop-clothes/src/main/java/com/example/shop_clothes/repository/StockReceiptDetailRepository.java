package com.example.shop_clothes.repository;

import com.example.shop_clothes.model.StockReceiptDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockReceiptDetailRepository extends JpaRepository<StockReceiptDetail,Long> {
}
