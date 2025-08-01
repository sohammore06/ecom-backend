package com.demo.ecommerce_backend.order;

import com.demo.ecommerce_backend.User.User;
import com.demo.ecommerce_backend.payment.PaymentMode;
import com.demo.ecommerce_backend.product.Product;
import com.demo.ecommerce_backend.util.JsonMapConverter;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    private BigDecimal paidAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    @Column(nullable = false)
    private boolean fulfilled = false;

    private String paymentReference;

    private boolean usedWallet;
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Product coupon;

    @Column
    private BigDecimal discountAmount;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();
    @PrePersist
    public void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }
    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
