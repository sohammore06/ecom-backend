package com.demo.ecommerce_backend.product;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {
    private String code;
    private String name;
    private String description;
    private String imageUrl;
    private Integer categoryId;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private boolean isActive;
}
