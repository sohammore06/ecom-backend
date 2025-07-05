package com.demo.ecommerce_backend.order;

import com.demo.ecommerce_backend.orderItem.OrderItemResponse;
import com.demo.ecommerce_backend.payment.PaymentMode;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Integer orderId;
    private Integer userId;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private OrderStatus status;
    private String paymentMode;
    private boolean usedWallet;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;
    private String paymentUrl;
    private boolean fulfilled;
}
