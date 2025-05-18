package com.demo.ecommerce_backend.policy;
import com.demo.ecommerce_backend.policycontent.PolicyContentDto;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyResponseDto {
    private Integer id;
    private String type;
    private String title;
    private List<PolicyContentDto> sections;
    private Boolean isActive;
}
