package com.demo.ecommerce_backend.policy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy,Integer> {

    Optional<Policy> findByType(String type);

}
