package com.example.shop_clothes.repository;

import com.example.shop_clothes.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem existsByProductDetail_Id(Long productDetailId);
    CartItem findByProductDetail_Id(Long productDetailId);

    List<CartItem> findByIdIn(Collection<Long> ids);
}
