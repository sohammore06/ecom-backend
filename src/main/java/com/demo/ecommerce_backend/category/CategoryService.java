package com.demo.ecommerce_backend.category;
import com.demo.ecommerce_backend.category.CategoryResponse;
import com.demo.ecommerce_backend.category.CategoryRequest;
import com.demo.ecommerce_backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
            category.setParent(parent);
        }

        Category saved = categoryRepository.save(category);
        return mapToResponse(saved);
    }

    public CategoryResponse getCategoryById(Integer id) {
        Category category = getCategoryEntityById(id);
        return mapToResponse(category);
    }

    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse updateCategory(Integer id, CategoryRequest request) {
        Category category = getCategoryEntityById(id);

        category.setName(request.getName());

        if (request.getParentId() != null) {
            if (id.equals(request.getParentId())) {
                throw new IllegalArgumentException("Category cannot be its own parent");
            }

            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
            category.setParent(parent);
        } else {
            category.setParent(null); // remove parent if null
        }

        Category updated = categoryRepository.save(category);
        return mapToResponse(updated);
    }

    public void deleteCategory(Integer id) {
        Category category = getCategoryEntityById(id);
        categoryRepository.delete(category);
    }

    private Category getCategoryEntityById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID " + id + " not found"));
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .isActive(category.isActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}


