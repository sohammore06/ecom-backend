package com.demo.ecommerce_backend.thirdparty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ThirdPartyResponse {
    private Integer id;
    private String name;
    private boolean active;
    private String metadata;
}
