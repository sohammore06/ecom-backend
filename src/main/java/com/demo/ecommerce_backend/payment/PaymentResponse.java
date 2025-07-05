package com.demo.ecommerce_backend.payment;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentResponse {
    private String transactionId;
    private String paymentUrl;
    private BigDecimal amount;
    private String status;
}
