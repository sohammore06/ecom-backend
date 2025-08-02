package com.demo.ecommerce_backend.smileone;

import lombok.Data;

@Data
public class SmileOneValidationResponse {
    private int status;
    private String username;
    private int zone;
    private double change_price;
    private String use;
    private String message;
}
