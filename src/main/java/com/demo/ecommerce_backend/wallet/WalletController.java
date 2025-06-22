package com.demo.ecommerce_backend.wallet;

import com.demo.ecommerce_backend.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<WalletResponse>> getWallet(@PathVariable Integer userId) {
        return ResponseEntity.ok(walletService.getWallet(userId));
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<ApiResponse<WalletResponse>> addAmount(@PathVariable Integer userId,
                                                                 @RequestBody WalletRequest request) {
        return ResponseEntity.ok(walletService.addAmount(userId, request));
    }
}
