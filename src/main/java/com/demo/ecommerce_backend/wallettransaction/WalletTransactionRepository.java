package com.demo.ecommerce_backend.wallettransaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Integer> {
    List<WalletTransaction> findByWalletId(Integer walletId);
}
