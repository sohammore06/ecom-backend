package com.demo.ecommerce_backend.merchant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MerchantResponse {
    private Integer id;
    private String name;
    private String upiId;
    private String paymentCreateUrl;
    private String paymentStatusUrl;
    private String redirectUrl;
    private boolean active;
}
