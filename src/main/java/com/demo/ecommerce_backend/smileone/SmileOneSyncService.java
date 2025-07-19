package com.demo.ecommerce_backend.smileone;

import com.demo.ecommerce_backend.auth.AuthenticationService;
import com.demo.ecommerce_backend.category.Category;
import com.demo.ecommerce_backend.category.CategoryRepository;
import com.demo.ecommerce_backend.product.Product;
import com.demo.ecommerce_backend.product.ProductRepository;
import com.demo.ecommerce_backend.product.ProductSource;
import com.demo.ecommerce_backend.thirdparty.ThirdParty;
import com.demo.ecommerce_backend.thirdparty.ThirdPartyRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmileOneSyncService {
    private static final Logger log = LoggerFactory.getLogger(SmileOneSyncService.class);
    private final ProductRepository productRepository;
    private final ThirdPartyRepository thirdPartyRepository;
    private final CategoryRepository categoryRepository;
    private final SmileOneTpClient smileOneTpClient;
    private final ObjectMapper objectMapper;

    private static final String SOURCE_NAME = "smileone";

    public void syncProducts() {
        log.info("➡️ Starting SmileOne product sync");
        Optional<ThirdParty> thirdPartyOpt = thirdPartyRepository.findByNameIgnoreCase(SOURCE_NAME);
        if (thirdPartyOpt.isEmpty()) {
            log.warn("SmileOne ThirdParty config not found");
            return;
        }
        ThirdParty config = thirdPartyOpt.get();
        Map<String, Object> metadata = parseMetadata(config.getMetadata());

        if (!metadata.containsKey("products")) {
            throw new RuntimeException("❌ No 'products' field found in ThirdParty metadata.");
        }

        @SuppressWarnings("unchecked")
        List<String> productsToSync = (List<String>) metadata.get("products");

        for (String productName : productsToSync) {
            // 🔁 Fetch product list from SmileOne
            log.info("➡️ Starting SmileOne product sync for --->"+productName);
            SmileOneProductListResponse response = smileOneTpClient.fetchProductList(config,productName);

            if (response == null || response.getStatus() != 200 || response.getData() == null) {
                log.warn("SmileOne sync failed: invalid response");
                continue;
            }

            List<SmileOneProductItem> externalProducts =
                    response.getData().getProduct();

            int added = 0, updated = 0;
            for (SmileOneProductItem item : externalProducts) {
                Optional<Product> existingOpt = productRepository.findByExternalProductIdAndSource(
                        item.getId(), ProductSource.SMILEONE
                );

                if (existingOpt.isPresent()) {
                    Product existing = existingOpt.get();
                    if (isProductChanged(existing, item)) {
                        updateProduct(existing, item);
                        productRepository.save(existing);
                        updated++;
                    }
                } else {
                    Product newProduct = buildProduct(item, config,productName);
                    productRepository.save(newProduct);
                    added++;
                }
            }

            log.info("SmileOne Sync completed: Added = {}, Updated = {}", added, updated);
        }

    }

    private boolean isProductChanged(Product product, SmileOneProductItem item) {
        BigDecimal price = new BigDecimal(item.getPrice());
        BigDecimal discount = new BigDecimal(item.getDiscount());
        BigDecimal discountedPrice = price.subtract(discount);

        return !product.getPrice().equals(price)
                || !product.getDiscountedPrice().equals(discountedPrice)
                || !product.getName().equals(item.getSpu());
    }

    private Product buildProduct(SmileOneProductItem item, ThirdParty config,String productName) {
        Map<String, Object> metadata = parseMetadata(config.getMetadata());
        BigDecimal price = new BigDecimal(item.getPrice());
        BigDecimal discount = new BigDecimal(item.getDiscount());
        BigDecimal discountedPrice = price.subtract(discount);
        Category category = categoryRepository.findByNameIgnoreCase(productName)
                .orElseThrow(() -> new RuntimeException("Category '" + productName + "' not found"));
        return Product.builder()
                .code("SMILEONE_" + item.getId())
                .name(item.getSpu())
                .description(item.getSpu())
                .imageUrl("") // SmileOne doesn't return image, optional default
                .category(category) // No categories in SmileOne
                .price(price)
                .discountedPrice(discountedPrice)
                .source(ProductSource.SMILEONE)
                .externalProductId(item.getId())
                .externalCategoryId(null)
                .instantDelivery(true)
                .requiresUserGameId(true)
                .requiresServerId(false)
                .isActive(true)
                .metadata(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private void updateProduct(Product product,SmileOneProductItem item) {
        BigDecimal price = new BigDecimal(item.getPrice());
        BigDecimal discount = new BigDecimal(item.getDiscount());
        BigDecimal discountedPrice = price.subtract(discount);

        product.setName(item.getSpu());
        product.setDescription(item.getSpu());
        product.setPrice(price);
        product.setDiscountedPrice(discountedPrice);
        product.setUpdatedAt(LocalDateTime.now());
    }

    private Map<String, Object> parseMetadata(String metadataJson) {
        try {
            return objectMapper.readValue(metadataJson, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("Failed to parse metadata JSON", e);
            return new HashMap<>();
        }
    }

}
