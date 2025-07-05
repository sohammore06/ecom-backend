package com.demo.ecommerce_backend.gateway.upigateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UpiGatewayRequest {

    @JsonProperty("key")
    private String key;

    @JsonProperty("client_txn_id")
    private String clientTxnId;

    @JsonProperty("amount")
    private String amount; //

    @JsonProperty("p_info")
    private String pInfo;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("customer_email")
    private String customerEmail;

    @JsonProperty("customer_mobile")
    private String customerMobile;

    @JsonProperty("redirect_url")
    private String redirectUrl;

    @JsonProperty("udf1")
    private String udf1;

    @JsonProperty("udf2")
    private String udf2;

    @JsonProperty("udf3")
    private String udf3;
}
