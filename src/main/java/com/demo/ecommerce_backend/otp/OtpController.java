package com.demo.ecommerce_backend.otp;
import com.demo.ecommerce_backend.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/otp")
@RequiredArgsConstructor
public class OtpController {
    private final OtpService otpService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendOtp(@RequestBody OtpRequest request) {
        boolean sent = otpService.sendOtp(request);

        if (sent) {
            return ResponseEntity.ok(new ApiResponse<>(true, "OTP sent successfully"));
        } else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to send OTP"));

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
}
