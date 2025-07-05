package com.demo.ecommerce_backend.merchant;

import lombok.Data;

@Data
public class MerchantRequest {
    private String name;
    private String upiId;
    private String paymentCreateUrl;
    private String paymentStatusUrl;
    private String redirectUrl;
    private String apiKey;
    private String apiSecret;
}
