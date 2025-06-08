package com.demo.ecommerce_backend.cart;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByUserIdAndProductId(Integer userId, Integer productId);
    List<CartItem> findByUserId(Integer userId);
    void deleteByUserId(Integer userId);

}
