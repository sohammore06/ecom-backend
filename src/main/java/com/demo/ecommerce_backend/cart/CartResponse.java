package com.demo.ecommerce_backend.cart;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private Integer id;                 // CartItem ID
    private Integer productId;
    private String productName;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private int quantity;
    private BigDecimal subtotal;
    private String imageUrl;
    private Map<String, String> metadata;
}
