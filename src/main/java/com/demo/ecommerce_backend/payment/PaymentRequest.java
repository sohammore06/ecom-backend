package com.demo.ecommerce_backend.payment;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private Integer userId;
    private Integer orderId;
    private BigDecimal amount;
    private PaymentType paymentType;
    private PaymentMode paymentMode;
}
