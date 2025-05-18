package com.demo.ecommerce_backend.policy;

import com.demo.ecommerce_backend.policycontent.PolicyContentDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyRequestDto {
    private String type;
    private String title;
    private List<PolicyContentDto> sections;
}
