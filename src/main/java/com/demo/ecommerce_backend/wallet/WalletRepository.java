package com.demo.ecommerce_backend.wallet;

import com.demo.ecommerce_backend.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    Optional<Wallet> findByUser(User user);
}
