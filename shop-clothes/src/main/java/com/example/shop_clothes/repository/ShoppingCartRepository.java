package com.example.shop_clothes.repository;

import com.example.shop_clothes.model.ShoppingCart;
import com.example.shop_clothes.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    ShoppingCart findByUserId(Long userId);

    ShoppingCart findByUser(User user);
}
