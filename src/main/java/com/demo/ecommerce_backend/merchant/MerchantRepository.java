package com.demo.ecommerce_backend.merchant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, Integer> {
    Optional<Merchant> findByActiveTrue();

    @Modifying
    @Transactional
    @Query("UPDATE Merchant m SET m.active = false WHERE m.active = true")
    void deactivateAll();

}
