package com.demo.ecommerce_backend.order;

import com.demo.ecommerce_backend.User.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    Page<Order> findAll(Pageable pageable);

    Page<Order> findByUser(User user, Pageable pageable);

}
