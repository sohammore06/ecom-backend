package com.demo.ecommerce_backend.policycontent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/policy-content")
@RequiredArgsConstructor
public class PolicyContentController {

    private final PolicyContentService policyContentService;

    @PostMapping
    public ResponseEntity<PolicyContentDto> addContent(@RequestBody PolicyContentDto dto) {
        System.out.println("Your value: ");
        return ResponseEntity.ok(policyContentService.addContent(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PolicyContentDto> getContent(@PathVariable Integer id) {
        return ResponseEntity.ok(policyContentService.getContentById(id));
    }

    @GetMapping("/by-policy/{policyId}")
    public ResponseEntity<List<PolicyContentDto>> getContentsByPolicy(@PathVariable Integer policyId) {
        return ResponseEntity.ok(policyContentService.getContentsByPolicyId(policyId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PolicyContentDto> updateContent(@PathVariable Integer id, @RequestBody PolicyContentDto dto) {
        return ResponseEntity.ok(policyContentService.updateContent(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable Integer id) {
        policyContentService.deleteContent(id);
        return ResponseEntity.noContent().build();
    }
}
