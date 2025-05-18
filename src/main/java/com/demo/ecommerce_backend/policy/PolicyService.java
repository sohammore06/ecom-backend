package com.demo.ecommerce_backend.policy;

import com.demo.ecommerce_backend.policycontent.PolicyContentDto;
import com.demo.ecommerce_backend.policy.PolicyResponseDto;
import com.demo.ecommerce_backend.policy.Policy;
import com.demo.ecommerce_backend.policycontent.PolicyContent;
import com.demo.ecommerce_backend.policycontent.PolicyContentRepository;
import com.demo.ecommerce_backend.policy.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PolicyService {
    private final PolicyRepository policyRepository;
    private final PolicyContentRepository policyContentRepository;
    public PolicyResponseDto addPolicy(PolicyRequestDto dto) {
        // Convert DTO to entity
        Policy policy = new Policy();
        policy.setType(dto.getType());
        policy.setTitle(dto.getTitle());
        policy.setIsActive(true); // or from DTO if you want

        // Save policy first to get ID
        Policy savedPolicy = policyRepository.save(policy);

        // Save sections if any
        if (dto.getSections() != null) {
            dto.getSections().forEach(sectionDto -> {
                PolicyContent content = new PolicyContent();
                content.setHeading(sectionDto.getHeading());
                content.setDescription(sectionDto.getDescription());
                content.setSortOrder(sectionDto.getSortOrder());
                content.setPolicy(savedPolicy);
                policyContentRepository.save(content);
            });
        }

        // Return saved policy as DTO
        return mapToDto(savedPolicy);
    }
    public PolicyResponseDto editPolicy(Integer id, PolicyRequestDto dto) {
        Policy existingPolicy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found with id " + id));

        existingPolicy.setTitle(dto.getTitle());
        existingPolicy.setType(dto.getType());

        // Optionally update isActive if you want

        // Update the policy
        policyRepository.save(existingPolicy);

//        // Update sections: Simplest way - delete old sections and save new
//        List<PolicyContent> oldContents = policyContentRepository.findByPolicyId(id);
//        policyContentRepository.deleteAll(oldContents);
//
//        if (dto.getSections() != null) {
//            dto.getSections().forEach(sectionDto -> {
//                PolicyContent content = new PolicyContent();
//                content.setHeading(sectionDto.getHeading());
//                content.setDescription(sectionDto.getDescription());
//                content.setSortOrder(sectionDto.getSortOrder());
//                content.setPolicy(existingPolicy);
//                policyContentRepository.save(content);
//            });
//        }

        return mapToDto(existingPolicy);
    }
    public void deletePolicy(Integer id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found with id " + id));
        // Optional: delete associated content first if cascade is not configured
        List<PolicyContent> contents = policyContentRepository.findByPolicyId(id);
        policyContentRepository.deleteAll(contents);
        policyRepository.delete(policy);
    }
    public List<PolicyResponseDto> getAllPolicies() {
        return policyRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    public PolicyResponseDto getPolicyById(Integer id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found with id " + id));
        return mapToDto(policy);
    }
    public PolicyResponseDto getPolicyWithSections(String type) {
        Policy policy = policyRepository.findByType(type)
                .orElseThrow(() -> new RuntimeException("Policy not found for type: " + type));

        List<PolicyContent> contents = policyContentRepository.findAllByPolicyOrderBySortOrderAsc(policy);

        List<PolicyContentDto> sectionDtos = contents.stream().map(content -> {
            PolicyContentDto dto = new PolicyContentDto();
            dto.setId(content.getId());
            dto.setHeading(content.getHeading());
            dto.setDescription(content.getDescription());
            dto.setSortOrder(content.getSortOrder());
            return dto;
        }).toList();

        PolicyResponseDto responseDto = new PolicyResponseDto();
        responseDto.setTitle(policy.getTitle());
        responseDto.setType(policy.getType());
        responseDto.setSections(sectionDtos);
        return responseDto;
    }

    // Utility method to map entity to response DTO
    private PolicyResponseDto mapToDto(Policy policy) {
        List<PolicyContentDto> sections = policyContentRepository.findByPolicyId(policy.getId())
                .stream()
                .map(content -> PolicyContentDto.builder()
                        .id(content.getId())
                        .heading(content.getHeading())
                        .policyId(content.getPolicy().getId())
                        .description(content.getDescription())
                        .sortOrder(content.getSortOrder())
                        .build()
                )
                .collect(Collectors.toList());

        return new PolicyResponseDto(
                policy.getId(),
                policy.getType(),
                policy.getTitle(),
                sections,
                policy.getIsActive()
        );
    }
}
