package com.demo.ecommerce_backend.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByCode(String code);
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Integer id);

}
