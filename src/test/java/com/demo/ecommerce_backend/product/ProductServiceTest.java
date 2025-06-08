package com.demo.ecommerce_backend.product;

import com.demo.ecommerce_backend.category.Category;
import com.demo.ecommerce_backend.category.CategoryRepository;
import com.demo.ecommerce_backend.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProductSuccess() {
        ProductRequest request = getSampleProductRequest();

        Category category = new Category();
        category.setId(1);
        category.setParent(new Category()); // marks it as subcategory

        when(productRepository.existsByCode("SAMPLE_CODE")).thenReturn(false);
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArguments()[0]);

        ProductResponse response = productService.createProduct(request);

        assertEquals("Sample", response.getName());
        assertEquals("SAMPLE_CODE", response.getCode());
    }

    @Test
    void testGetProductById_Success() {
        Product product = getSampleProduct();
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        ProductResponse response = productService.getProductById(1);
        assertEquals("Sample", response.getName());
    }
    @Test
    void testGetAllProducts() {
        List<Product> products = List.of(getSampleProduct(), getSampleProduct());
        when(productRepository.findAll()).thenReturn(products);

        List<ProductResponse> responses = productService.getAllProducts(0,10);

        assertEquals(2, responses.size());
        assertEquals("Sample", responses.get(0).getName());
        assertEquals("SAMPLE_CODE", responses.get(0).getCode());
    }


    @Test
    void testGetProductById_NotFound() {
        when(productRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99));
    }

    @Test
    void testUpdateProduct_Success() {
        Product existing = getSampleProduct();
        ProductRequest updateRequest = getSampleProductRequest();
        updateRequest.setName("Updated");

        Category newCategory = new Category();
        newCategory.setId(1);
        newCategory.setParent(new Category());

        when(productRepository.findById(1)).thenReturn(Optional.of(existing));
        when(productRepository.existsByCodeAndIdNot("SAMPLE_CODE", 1)).thenReturn(false);
        when(categoryRepository.findById(1)).thenReturn(Optional.of(newCategory));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArguments()[0]);

        ProductResponse response = productService.updateProduct(1, updateRequest);
        assertEquals("Updated", response.getName());
    }

    @Test
    void testDeleteProduct() {
        Product product = getSampleProduct();
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        productService.deleteProduct(1);

        verify(productRepository, times(1)).delete(product);
    }

    private ProductRequest getSampleProductRequest() {
        return ProductRequest.builder()
                .name("Sample")
                .code("SAMPLE_CODE")
                .description("desc")
                .imageUrl("url")
                .price(BigDecimal.valueOf(100))
                .discountedPrice(BigDecimal.valueOf(90))
                .categoryId(1)
                .isActive(true)
                .build();
    }

    private Product getSampleProduct() {
        Category category = new Category();
        category.setId(1);
        category.setParent(new Category());

        return Product.builder()
                .id(1)
                .name("Sample")
                .code("SAMPLE_CODE")
                .description("desc")
                .imageUrl("url")
                .price(BigDecimal.valueOf(100))
                .discountedPrice(BigDecimal.valueOf(90))
                .isActive(true)
                .category(category)
                .build();
    }
}
