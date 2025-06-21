package com.demo.ecommerce_backend.product;

import com.demo.ecommerce_backend.category.Category;
import com.demo.ecommerce_backend.category.CategoryRepository;
import com.demo.ecommerce_backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Product code already exists.");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Product product = Product.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .category(category)
                .price(request.getPrice())
                .discountedPrice(request.getDiscountedPrice())
                .isActive(request.isActive())
                .instantDelivery(request.isInstantDelivery())
                .build();

        return mapToResponse(productRepository.save(product));
    }

    public ProductResponse getProductById(Integer id) {
        return mapToResponse(getProductEntityById(id));
    }

    public List<ProductResponse> getAllProducts(int page, int size,Boolean active, Boolean instantDelivery) {
        Pageable pageable = PageRequest.of(page, size);
        List<Product> products;

        if (active != null && instantDelivery != null) {
            products = productRepository.findByIsActiveAndInstantDelivery(active, instantDelivery, pageable);
        } else if (active != null) {
            products = productRepository.findByIsActive(active, pageable);
        } else if (instantDelivery != null) {
            products = productRepository.findByInstantDelivery(instantDelivery, pageable);
        } else {
            products = productRepository.findAll(pageable).getContent();
        }
        return products.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse updateProduct(Integer id, ProductRequest request) {
        Product product = getProductEntityById(id);

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setPrice(request.getPrice());
        product.setDiscountedPrice(request.getDiscountedPrice());
        product.setActive(request.isActive());
        if (productRepository.existsByCodeAndIdNot(request.getCode(), id)) {
            throw new IllegalArgumentException("Product code already exists.");
        }

        product.setCode(request.getCode());

        if (!product.getCategory().getId().equals(request.getCategoryId())) {
            Category newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

            product.setCategory(newCategory);
        }

        return mapToResponse(productRepository.save(product));
    }

    public void deleteProduct(Integer id) {
        Product product = getProductEntityById(id);
        productRepository.delete(product);
    }

    private Product getProductEntityById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found"));
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .categoryId(product.getCategory().getId())
                .price(product.getPrice())
                .discountedPrice(product.getDiscountedPrice())
                .isActive(product.isActive())
                .instantDelivery(product.isInstantDelivery())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
