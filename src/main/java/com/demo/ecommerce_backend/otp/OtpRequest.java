package com.demo.ecommerce_backend.otp;
import lombok.Data;

@Data
public class OtpRequest {
    private String number;
    private String customerName;
}
