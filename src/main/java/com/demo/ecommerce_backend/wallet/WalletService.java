package com.demo.ecommerce_backend.wallet;

import com.demo.ecommerce_backend.User.User;
import com.demo.ecommerce_backend.User.UserReposirtory;
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

    public ApiResponse<WalletResponse> getWallet(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseGet(() -> createWalletForUser(user));

        return new ApiResponse<>(true, "Wallet fetched successfully", toResponse(wallet));
    }

    public ApiResponse<WalletResponse> addAmount(Integer userId, WalletRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseGet(() -> createWalletForUser(user));

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        wallet.setLastUpdated(LocalDateTime.now());
        walletRepository.save(wallet);

        return new ApiResponse<>(true, "Amount added to wallet", toResponse(wallet));
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
