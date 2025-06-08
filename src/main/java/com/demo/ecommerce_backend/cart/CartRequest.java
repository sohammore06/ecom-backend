package com.demo.ecommerce_backend.cart;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartRequest {
    private Integer userId;
    private Integer productId;
    private int quantity;
}