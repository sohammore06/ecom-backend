package com.demo.ecommerce_backend.orderItem;

import com.demo.ecommerce_backend.order.Order;
import com.demo.ecommerce_backend.product.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal priceAtPurchase;

    private String externalOrderId;

    private boolean delivered;

    @Column(columnDefinition = "TEXT")
    private String deliveryMetadata;

    @Column(name = "game_user_id")
    private String gameUserId;

    @Column(name = "zone_id")
    private String zoneId;

    private String fulfillmentStatus; // e.g., PENDING, SUCCESS, FAILED
    private String fulfillmentError;
}
