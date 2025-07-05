package com.demo.ecommerce_backend.merchant;

import com.demo.ecommerce_backend.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/merchant")
@RequiredArgsConstructor
public class MerchantController {
    @PostMapping
    public ResponseEntity<ApiResponse<MerchantResponse>> createMerchant(
            @RequestBody MerchantRequest request) {
        return ResponseEntity.ok(merchantService.createMerchant(request));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<MerchantResponse>>> getAllMerchants() {
        return ResponseEntity.ok(merchantService.getAllMerchants());
    }

    private final MerchantService merchantService;
    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Void>> activateMerchant(@PathVariable Integer id) {
        merchantService.activateMerchant(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Merchant activated successfully", null));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<MerchantResponse>> getActiveMerchant() {
        return ResponseEntity.ok(merchantService.getActiveMerchant());
    }
}
