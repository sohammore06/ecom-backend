package com.demo.ecommerce_backend.category;
import com.demo.ecommerce_backend.auth.AuthenticationService;
import com.demo.ecommerce_backend.category.CategoryResponse;
import com.demo.ecommerce_backend.category.CategoryRequest;
import com.demo.ecommerce_backend.exception.ResourceNotFoundException;
import com.demo.ecommerce_backend.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    private final CategoryRepository categoryRepository;
    private final FileUploadUtil fileUploadUtil;
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setType(request.getType()); // ➕ added
        category.setExternalCategoryId(request.getExternalCategoryId()); // ➕ added
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
            category.setParent(parent);
        }
        if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            try {
                String imageUrl = fileUploadUtil.saveImage(request.getImageFile(), "categories");
                category.setImageUrl(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload category image", e);
            }
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
        category.setType(request.getType()); // ➕ added
        category.setExternalCategoryId(request.getExternalCategoryId()); // ➕ added
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
        if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            try {
                log.info("Updating category image");
                String imageUrl = fileUploadUtil.saveImage(request.getImageFile(), "categories");
                category.setImageUrl(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload category image", e);
            }
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
                .imageUrl(category.getImageUrl()) // ➕ include in CategoryResponse if not already
                .type(category.getType()) // ➕
                .externalCategoryId(category.getExternalCategoryId()) // ➕
                .build();
    }
}


