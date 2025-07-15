package com.demo.ecommerce_backend.product;

import com.demo.ecommerce_backend.category.Category;
import com.demo.ecommerce_backend.util.JsonMapConverter;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(length = 1000)
    private String imageUrl; // ✅ URL to image (can be S3, CDN, etc.)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private BigDecimal discountedPrice;

    private boolean isActive = true;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean instantDelivery ;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductSource source;

    private String externalProductId;
    private String externalCategoryId;
    private boolean requiresUserGameId;
    private boolean requiresServerId;
    @Convert(converter = JsonMapConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, String> metadata;
    @PrePersist
    public void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }
}
