package com.demo.ecommerce_backend.usercoupon;

import com.demo.ecommerce_backend.User.User;
import com.demo.ecommerce_backend.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Integer> {
    Optional<UserCoupon> findByUserAndCouponAndUsedFalse(User user, Product coupon);
}

