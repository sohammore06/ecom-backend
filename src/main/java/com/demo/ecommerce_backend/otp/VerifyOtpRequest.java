package com.demo.ecommerce_backend.otp;
import lombok.Data;

@Data
public class VerifyOtpRequest {
    private String phoneNo;
    private String otp;
}
