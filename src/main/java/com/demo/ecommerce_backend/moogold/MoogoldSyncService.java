package com.demo.ecommerce_backend.moogold;
import com.demo.ecommerce_backend.category.Category;
import com.demo.ecommerce_backend.category.CategoryRepository;
import com.demo.ecommerce_backend.product.Product;
import com.demo.ecommerce_backend.product.ProductRepository;
import com.demo.ecommerce_backend.product.ProductSource;
import com.demo.ecommerce_backend.thirdparty.ThirdParty;
import com.demo.ecommerce_backend.thirdparty.ThirdPartyRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoogoldSyncService {
    private final MoogoldTpClient moogoldTpClient;
    private final ThirdPartyRepository thirdPartyRepository;
    private final ObjectMapper objectMapper;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    public void syncProducts(ThirdParty moogoldConfig) {
        log.info("➡️ Starting MooGold product sync");

        Map<String, Object> metadata = parseMetadata(moogoldConfig.getMetadata());
        List<Integer> productIds = (List<Integer>) metadata.get("products");

        Category category = categoryRepository.findByNameIgnoreCase("mobilelegends")
                .orElseThrow(() -> new RuntimeException("Category 'mobilelegends' not found"));

        for (Integer productId : productIds) {
            try {
                MoogoldProductListResponse response = moogoldTpClient.fetchProductList(productId);
                if (response == null || response.getVariations() == null) {
                    log.warn("❌ Empty response for productId: {}", productId);
                    continue;
                }

                for (VariationDto variation : response.getVariations()) {
                    Optional<Product> existing = productRepository.findByExternalProductIdAndSource(
                            variation.getId().toString(), ProductSource.MOOGOLD
                    );

                    BigDecimal price = BigDecimal.valueOf(variation.getPrice());
                    String description = variation.getName();

                    if (existing.isPresent()) {
                        Product product = existing.get();
                        if (!product.getPrice().equals(price) || !product.getName().equals(variation.getName())) {
                            product.setPrice(price);
                            product.setDiscountedPrice(price);
                            product.setName(variation.getName());
                            product.setDescription(description);
                            product.setUpdatedAt(LocalDateTime.now());
                            productRepository.save(product);
                        }
                    } else {
                        Product product = Product.builder()
                                .code("MOOGOLD_" + variation.getId())
                                .name(variation.getName())
                                .description(description)
                                .imageUrl(response.getImageUrl())
                                .category(category)
                                .price(price)
                                .discountedPrice(price)
                                .source(ProductSource.MOOGOLD)
                                .externalProductId(variation.getId().toString())
                                .externalCategoryId(String.valueOf(productId))
                                .instantDelivery(true)
                                .requiresUserGameId(true)
                                .requiresServerId(true)
                                .isActive(true)
                                .metadata(null)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                        productRepository.save(product);
                    }
                }
            } catch (Exception e) {
                log.error("❌ Failed to sync MooGold product for id: {}", productId, e);
            }
        }
    }

    private Map<String, Object> parseMetadata(String metadataJson) {
        try {
            return objectMapper.readValue(metadataJson, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.error("Failed to parse metadata JSON", e);
            return new HashMap<>();
        }
    }

}
