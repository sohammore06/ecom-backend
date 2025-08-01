package com.demo.ecommerce_backend.category;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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
    private MultipartFile imageFile;
    private CategoryType type; // ➕ added this
    private String externalCategoryId; // ➕
}