package com.demo.ecommerce_backend.payment;

import com.demo.ecommerce_backend.gateway.GatewayStatusResponse;
import com.demo.ecommerce_backend.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.createPayment(request));
    }

    @GetMapping("/status/{txnId}")
    public ResponseEntity<ApiResponse<GatewayStatusResponse>> getPaymentStatus(@PathVariable String txnId) {
        return ResponseEntity.ok(paymentService.checkAndProcessPaymentStatus(txnId));
    }
}

