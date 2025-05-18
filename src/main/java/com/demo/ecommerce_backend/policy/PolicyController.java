package com.demo.ecommerce_backend.policy;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/policy")
@RequiredArgsConstructor
public class PolicyController {
    private final PolicyService policyService;

    @PostMapping
    public ResponseEntity<PolicyResponseDto> addPolicy(@RequestBody PolicyRequestDto dto) {
        PolicyResponseDto savedPolicy = policyService.addPolicy(dto);
        return ResponseEntity.ok(savedPolicy);
    }

    // Edit existing policy by id
    @PutMapping("/{id}")
    public ResponseEntity<PolicyResponseDto> editPolicy(
            @PathVariable Integer id,
            @RequestBody PolicyRequestDto dto) {
        PolicyResponseDto updatedPolicy = policyService.editPolicy(id, dto);
        return ResponseEntity.ok(updatedPolicy);
    }

    // Delete policy by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable Integer id) {
        policyService.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }

    //  Get all policies
    @GetMapping
    public ResponseEntity<List<PolicyResponseDto>> getAllPolicies() {
        List<PolicyResponseDto> policies = policyService.getAllPolicies();
        return ResponseEntity.ok(policies);
    }

    @GetMapping("/{type}")
    public ResponseEntity<PolicyResponseDto> getPolicyByType(@PathVariable String type) {
        return ResponseEntity.ok(policyService.getPolicyWithSections(type));
    }

}
