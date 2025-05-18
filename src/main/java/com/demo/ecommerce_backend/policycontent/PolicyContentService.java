package com.demo.ecommerce_backend.policycontent;


import com.demo.ecommerce_backend.policy.Policy;
import com.demo.ecommerce_backend.policy.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PolicyContentService {
    private final PolicyContentRepository repository;
    private final PolicyRepository policyRepository;

    public PolicyContentDto addContent(PolicyContentDto dto) {
        System.out.println("Your value: " + dto);
        Policy policy = policyRepository.findById(dto.getPolicyId())
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        PolicyContent content = new PolicyContent();
        content.setHeading(dto.getHeading());
        content.setDescription(dto.getDescription());
        content.setSortOrder(dto.getSortOrder());
        content.setPolicy(policy);

        return mapToDto(repository.save(content));
    }

    public PolicyContentDto getContentById(Integer id) {
        PolicyContent content = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Content not found"));
        return mapToDto(content);
    }

    public List<PolicyContentDto> getContentsByPolicyId(Integer policyId) {
        return repository.findByPolicyId(policyId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public PolicyContentDto updateContent(Integer id, PolicyContentDto dto) {
        PolicyContent content = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Content not found"));

        content.setHeading(dto.getHeading());
        content.setDescription(dto.getDescription());
        content.setSortOrder(dto.getSortOrder());

        return mapToDto(repository.save(content));
    }

    public void deleteContent(Integer id) {
        repository.deleteById(id);
    }

    private PolicyContentDto mapToDto(PolicyContent content) {
        return PolicyContentDto.builder()
                .id(content.getId())
                .policyId(content.getPolicy().getId())
                .heading(content.getHeading())
                .description(content.getDescription())
                .sortOrder(content.getSortOrder())
                .build();
    }

}
