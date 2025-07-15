package com.demo.ecommerce_backend.gateway.mobalegends;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MobalegendsWebhookRequest {
    private String transactionId;
    private String status;
    private BigDecimal amount;
    private String customerName;
    private String merchantName;
    private String pInfo;
    private String timestamp;
}
