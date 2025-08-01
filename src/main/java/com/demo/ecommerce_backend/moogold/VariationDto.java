package com.demo.ecommerce_backend.moogold;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VariationDto {
    @JsonProperty("variation_name")
    private String name;

    @JsonProperty("variation_id")
    private Long id;

    @JsonProperty("variation_price")
    private Double price;
}
