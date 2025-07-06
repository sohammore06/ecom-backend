package com.demo.ecommerce_backend.otp;
import com.demo.ecommerce_backend.auth.AuthenticationResponse;
import com.demo.ecommerce_backend.oneApi.OneApiOtpClient;
import com.demo.ecommerce_backend.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/api/v1/auth/otp")
@RequiredArgsConstructor
public class OtpController {
    private final OtpService otpService;
    private static final Logger log = LoggerFactory.getLogger(OneApiOtpClient.class);
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendOtp(@RequestBody OtpRequest request) {
        OtpResponse response = otpService.sendOtp(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(new ApiResponse<>(true, response.getMessage()));
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, response.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@RequestBody VerifyOtpRequest request) {
        boolean isValid = otpService.verifyOtp(request.getPhoneNo(), request.getOtp());
        if (isValid) {
            return ResponseEntity.ok(new ApiResponse<>(true, "OTP Verified", null));
        } else {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse<>(false, "Invalid OTP", null));
        }
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> loginViaOtp(@RequestBody VerifyOtpRequest request) {
        try {
            AuthenticationResponse response = otpService.loginViaOtp(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", response));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

}
