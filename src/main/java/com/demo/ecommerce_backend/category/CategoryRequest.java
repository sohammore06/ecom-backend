package com.demo.ecommerce_backend.category;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CategoryRequest {
    @NotBlank(message = "Category name is required")
    private String name;
    private Integer parentId; // null if it's a top-level category
    private boolean isActive = true;
}