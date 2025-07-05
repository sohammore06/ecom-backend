package com.demo.ecommerce_backend.payment;

import com.demo.ecommerce_backend.User.User;
import com.demo.ecommerce_backend.merchant.Merchant;
import com.demo.ecommerce_backend.order.Order;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Order order;

    @ManyToOne
    private Merchant merchant;

    @Column(nullable = false, unique = true)
    private String transactionId;

    @Column(nullable = false)
    private BigDecimal amount;

    private String status; // e.g., CREATED, PENDING, SUCCESS, FAILED

    private String paymentUrl;
    @Column(columnDefinition = "TEXT")
    private String requestPayload;

    @Column(columnDefinition = "TEXT")
    private String responsePayload;
    @Column(nullable = false)

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType; // ORDER or WALLET

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode", nullable = false)
    private PaymentMode paymentMode;


    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    @PrePersist
    public void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }
    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
