package com.demo.ecommerce_backend.thirdparty;

import lombok.Data;

import java.util.Map;

@Data
public class ThirdPartyRequest {
    private String name;
    private boolean active;
    private Map<String, Object> metadata;
}
