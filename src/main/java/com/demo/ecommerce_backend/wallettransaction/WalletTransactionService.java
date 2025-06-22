package com.demo.ecommerce_backend.wallettransaction;

import com.demo.ecommerce_backend.User.User;
import com.demo.ecommerce_backend.User.UserReposirtory;
import com.demo.ecommerce_backend.util.ApiResponse;
import com.demo.ecommerce_backend.wallet.Wallet;
import com.demo.ecommerce_backend.wallet.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletTransactionService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final UserReposirtory userRepository;

    public ApiResponse<List<WalletTransactionResponse>> getUserTransactions(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Wallet wallet = walletRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Wallet not found"));

        List<WalletTransaction> transactions = transactionRepository.findByWalletId(wallet.getId());

        List<WalletTransactionResponse> responseList = transactions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new ApiResponse<>(true, "Transactions fetched successfully", responseList);
    }

    public void logTransaction(Wallet wallet, BigDecimal amount, String description, TransactionType type) {
        WalletTransaction tx = WalletTransaction.builder()
                .wallet(wallet)
                .amount(amount)
                .description(description)
                .type(type)
                .timestamp(LocalDateTime.now())
                .build();
        transactionRepository.save(tx);
    }

    private WalletTransactionResponse toResponse(WalletTransaction tx) {
        return WalletTransactionResponse.builder()
                .transactionId(tx.getId())
                .amount(tx.getAmount())
                .description(tx.getDescription())
                .type(tx.getType())
                .timestamp(tx.getTimestamp())
                .build();
    }
}
