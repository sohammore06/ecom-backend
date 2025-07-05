package com.demo.ecommerce_backend.order;

import com.demo.ecommerce_backend.orderItem.OrderItemRequest;
import com.demo.ecommerce_backend.payment.PaymentMode;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private List<OrderItemRequest> items;
    private PaymentMode paymentMode; // WALLET, UPI, etc.
    private boolean useWallet;
}