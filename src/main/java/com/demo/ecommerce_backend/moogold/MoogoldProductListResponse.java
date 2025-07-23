package com.demo.ecommerce_backend.moogold;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MoogoldProductListResponse {
    @JsonProperty("Product_Name")
    private String productName;

    @JsonProperty("Image_URL")
    private String imageUrl;

    @JsonProperty("Variation")
    private List<VariationDto> variations;

    @JsonProperty("fields")
    private List<String> fields;
}
