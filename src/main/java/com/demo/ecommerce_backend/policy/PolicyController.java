package com.demo.ecommerce_backend.policy;

import com.demo.ecommerce_backend.util.ApiResponse;
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
    public ResponseEntity<ApiResponse<PolicyResponseDto>> addPolicy(@RequestBody PolicyRequestDto dto) {
        PolicyResponseDto savedPolicy = policyService.addPolicy(dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Policy created successfully", savedPolicy));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PolicyResponseDto>> editPolicy(
            @PathVariable Integer id,
            @RequestBody PolicyRequestDto dto) {
        PolicyResponseDto updatedPolicy = policyService.editPolicy(id, dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Policy updated successfully", updatedPolicy));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePolicy(@PathVariable Integer id) {
        policyService.deletePolicy(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Policy deleted successfully", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PolicyResponseDto>>> getAllPolicies() {
        List<PolicyResponseDto> policies = policyService.getAllPolicies();
        return ResponseEntity.ok(new ApiResponse<>(true, "Fetched all policies", policies));
    }

    @GetMapping("/{type}")
    public ResponseEntity<ApiResponse<PolicyResponseDto>> getPolicyByType(@PathVariable String type) {
        PolicyResponseDto policy = policyService.getPolicyWithSections(type);
        return ResponseEntity.ok(new ApiResponse<>(true, "Fetched policy for type: " + type, policy));
    }

}
