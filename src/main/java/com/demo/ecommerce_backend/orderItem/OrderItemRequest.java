package com.demo.ecommerce_backend.orderItem;

import lombok.Data;


@Data
public class OrderItemRequest {
    private Integer productId;
    private Integer quantity;
}
