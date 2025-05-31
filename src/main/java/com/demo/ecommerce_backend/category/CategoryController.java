package com.demo.ecommerce_backend.category;

import com.demo.ecommerce_backend.category.CategoryRequest;
import com.demo.ecommerce_backend.category.CategoryResponse;
import com.demo.ecommerce_backend.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Category created successfully", response)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Categories fetched successfully", categories)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Integer id) {
        CategoryResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Category fetched successfully", response)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Integer id,
            @Valid  @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Category updated successfully", response)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Category deleted successfully", null)
        );
    }
}

