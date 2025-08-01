package com.demo.ecommerce_backend.usercoupon;

import com.demo.ecommerce_backend.User.User;
import com.demo.ecommerce_backend.product.Product;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_coupon")
public class UserCoupon {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    private Product coupon;

    private boolean used = false;
    private LocalDateTime purchasedAt;
}

