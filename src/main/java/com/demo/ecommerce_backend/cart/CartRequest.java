package com.demo.ecommerce_backend.cart;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartRequest {
    private Integer userId;
    private Integer productId;
    private int quantity;
    private String imageUrl;
    private Map<String, String> metadata;
}