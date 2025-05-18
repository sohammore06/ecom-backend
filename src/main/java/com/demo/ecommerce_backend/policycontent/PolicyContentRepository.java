package com.demo.ecommerce_backend.policycontent;

import com.demo.ecommerce_backend.policy.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface PolicyContentRepository extends JpaRepository<PolicyContent,Integer> {
    List<PolicyContent> findAllByPolicyOrderBySortOrderAsc(Policy policy);
    List<PolicyContent> findByPolicyId(Integer policyId);
}
