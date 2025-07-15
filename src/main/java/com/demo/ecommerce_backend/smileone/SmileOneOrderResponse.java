package com.demo.ecommerce_backend.smileone;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
public class SmileOneOrderResponse {
    private int status;
    private String message;

    @JsonProperty("order_id")
    private String orderId;
}
