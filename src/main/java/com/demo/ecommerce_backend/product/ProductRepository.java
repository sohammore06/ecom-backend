package com.demo.ecommerce_backend.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByCode(String code);
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Integer id);
    Page<Product> findAll(Pageable pageable);
    List<Product> findByIsActiveAndInstantDelivery(boolean isActive, boolean instantDelivery, Pageable pageable);
    List<Product> findByIsActive(boolean isActive, Pageable pageable);
    List<Product> findByInstantDelivery(boolean instantDelivery, Pageable pageable);
    List<Product> findBySource(ProductSource source);
    List<Product> findBySourceAndIsActive(ProductSource source, boolean isActive);
}
