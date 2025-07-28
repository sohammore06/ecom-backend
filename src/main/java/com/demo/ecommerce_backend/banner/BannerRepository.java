package com.demo.ecommerce_backend.banner;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Integer> {
    List<Banner> findAllByActiveTrueOrderBySortOrderAsc();
}
