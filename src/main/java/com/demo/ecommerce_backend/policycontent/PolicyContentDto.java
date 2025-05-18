package com.demo.ecommerce_backend.policycontent;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyContentDto {
    private Integer id;
    private String heading;
    private String description;
    private int sortOrder;
    private Integer policyId;
}
