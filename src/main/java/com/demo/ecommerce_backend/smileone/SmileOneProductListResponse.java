package com.demo.ecommerce_backend.smileone;

import lombok.Data;

import java.util.List;

@Data
public class SmileOneProductListResponse {
    private int status;
    private String message;
    private SmileOneProductData data;

    @Data
    public static class SmileOneProductData {
        private List<SmileOneProductItem> product;
    }
}
