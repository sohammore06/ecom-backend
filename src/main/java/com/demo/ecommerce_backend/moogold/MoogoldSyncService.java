package com.demo.ecommerce_backend.moogold;
import com.demo.ecommerce_backend.category.Category;
import com.demo.ecommerce_backend.category.CategoryRepository;
import com.demo.ecommerce_backend.category.CategoryType;
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

        List<Category> gameCategories = categoryRepository.findByTypeAndExternalCategoryIdIsNotNull(CategoryType.GAME);

        for (Category category : gameCategories) {
            String externalCategoryId = category.getExternalCategoryId();
            if (externalCategoryId == null) continue;
            try {
                int moogoldCategoryId = Integer.parseInt(externalCategoryId);
                log.info("✅ starting Synced products for category id",moogoldCategoryId );
                MoogoldProductListResponse response = moogoldTpClient.fetchProductList(moogoldCategoryId);
                if (response == null || response.getVariations() == null) {
                    log.warn("❌ Empty response for productId: {}", moogoldCategoryId);
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
                                .externalCategoryId(String.valueOf(moogoldCategoryId))
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
                Map<String, String> serverList = moogoldTpClient.fetchServerList(moogoldCategoryId);
                log.info("✅ Synced {} products for category '{}'", response.getVariations().size(), category.getName());
            }catch (NumberFormatException e) {
                log.error("❌ Invalid externalCategoryId '{}' for category '{}'", externalCategoryId, category.getName());
                continue;
            }
            catch (Exception e) {
                log.error("❌ Failed to sync MooGold product for id: {}", category.getExternalCategoryId(), e);
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
