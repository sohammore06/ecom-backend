package com.demo.ecommerce_backend.gateway;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class GatewayStatusResponse {
    private String transactionId;
    private String status;         // e.g. SUCCESS, FAILED, etc.
    private Map<String, Object> rawResponse;    // Store full JSON string for audit/debug
}
