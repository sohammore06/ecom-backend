package com.demo.ecommerce_backend.thirdparty;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThirdPartyRepository extends JpaRepository<ThirdParty, Integer> {
    boolean existsByNameIgnoreCase(String name);
    Optional<ThirdParty> findByNameIgnoreCase(String name);
}
