package com.demo.ecommerce_backend.gateway.mobalegends;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MobalegendsGatewayRequest {
    private BigDecimal amount;
    private String apiKey;
    private String merchantName;
    private String upiId;
    private String customerName;
    private String customerEmail;
    private String customerMobile;
    private String redirectUrl;
    private String pInfo;
    private String udf1;
    private String udf2;
    private String udf3;
}
