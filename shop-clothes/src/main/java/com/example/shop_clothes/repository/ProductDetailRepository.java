package com.example.shop_clothes.repository;

import com.example.shop_clothes.model.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetail,Long> {
    List<ProductDetail> findByProduct_Id(Long productId);
}
