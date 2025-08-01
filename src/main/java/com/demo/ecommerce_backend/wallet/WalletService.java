package com.demo.ecommerce_backend.wallet;

import com.demo.ecommerce_backend.User.User;
import com.demo.ecommerce_backend.User.UserReposirtory;
import com.demo.ecommerce_backend.payment.*;
import com.demo.ecommerce_backend.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserReposirtory userRepository;
    private final PaymentService paymentService;
    public ApiResponse<WalletResponse> getWallet(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseGet(() -> createWalletForUser(user));

        return new ApiResponse<>(true, "Wallet fetched successfully", toResponse(wallet));
    }

    public ApiResponse<PaymentResponse> addAmount(Integer userId, WalletRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseGet(() -> createWalletForUser(user));

        // 2. Use paymentService to initiate the payment
        ApiResponse<PaymentResponse> paymentResponse = paymentService.createWalletPayment(userId,request.getAmount());

        // 3. Return UPI payment URL
        if (paymentResponse.getData() != null) {
            return new ApiResponse<>(true, "Wallet recharge initiated", paymentResponse.getData());
        } else {
            return new ApiResponse<>(false, "Failed to initiate wallet recharge: " + paymentResponse.getMessage(), null);
        }
    }

    private Wallet createWalletForUser(User user) {
        Wallet wallet = Wallet.builder()
                .user(user)
                .balance(BigDecimal.ZERO)
                .lastUpdated(LocalDateTime.now())
                .build();
        return walletRepository.save(wallet);
    }

    private WalletResponse toResponse(Wallet wallet) {
        return WalletResponse.builder()
                .walletId(wallet.getId())
                .balance(wallet.getBalance())
                .build();
    }
}
