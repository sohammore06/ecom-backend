package com.demo.ecommerce_backend.payment;

import com.demo.ecommerce_backend.User.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByTransactionId(String transactionId);
    Page<Payment> findByUser(User user, Pageable pageable);
    Page<Payment> findByUserId(Long userId, Pageable pageable);
    List<Payment> findByUserIdOrderByCreatedAtDesc(Long userId);

}
