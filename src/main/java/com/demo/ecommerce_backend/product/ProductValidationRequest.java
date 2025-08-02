package com.demo.ecommerce_backend.product;
import lombok.Data;

@Data
public class ProductValidationRequest {
    private Integer productId;
    private String userId;
    private String zoneId; // optiona
}
