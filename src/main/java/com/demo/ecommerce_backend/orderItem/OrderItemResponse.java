package com.demo.ecommerce_backend.orderItem;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {
    private Integer productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private boolean delivered;
}
