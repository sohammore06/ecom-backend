package com.demo.ecommerce_backend.smileone;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class SmileOneOrderRequest {
    private String userId;       // entered by user
    private String zoneId;       // entered by user
    private String productName;  // e.g., "mobilelegends"
    private String productId;    // from SmileOne product list
}
