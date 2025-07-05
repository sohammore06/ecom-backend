package com.demo.ecommerce_backend.gateway;

import com.demo.ecommerce_backend.merchant.Merchant;

import java.math.BigDecimal;
import java.util.Map;

public interface GatewayClient<T> {

    /**
     * Initiates a payment using the given merchant and parameters.
     *
     * @param merchant  Active merchant configuration
     * @param params    Payload containing gateway-specific payment details
     * @return          A map containing response data (or a generic response wrapper)
     */
    Map<String, Object> initiatePayment(Merchant merchant, T params);
    GatewayStatusResponse checkStatus(Merchant merchant, String transactionId);
}
