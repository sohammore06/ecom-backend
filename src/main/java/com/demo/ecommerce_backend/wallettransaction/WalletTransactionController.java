package com.demo.ecommerce_backend.wallettransaction;

import com.demo.ecommerce_backend.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wallet/transactions")
@RequiredArgsConstructor
public class WalletTransactionController {

    private final WalletTransactionService walletTransactionService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<WalletTransactionResponse>>> getTransactions(@PathVariable Integer userId) {
        return ResponseEntity.ok(walletTransactionService.getUserTransactions(userId));
    }
}
