package com.demo.ecommerce_backend.product;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Integer id;
    private String code;
    private String name;
    private String description;
    private String imageUrl;
    private Integer categoryId;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private boolean isActive;
    private boolean instantDelivery;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProductSource source;
    private String externalProductId;
    private String externalCategoryId;
    private boolean requiresUserGameId;
    private boolean requiresServerId;
    private Map<String, String> metadata;
}
