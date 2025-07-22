package com.demo.ecommerce_backend.moogold;

import com.demo.ecommerce_backend.thirdparty.ThirdParty;
import com.demo.ecommerce_backend.thirdparty.ThirdPartyRepository;
import com.demo.ecommerce_backend.util.MoogoldUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class MoogoldTpClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    private final ThirdPartyRepository thirdPartyRepository;

    private static final String GET_PRODUCTS_URL = "https://moogold.com/wp-json/v1/api/product/product_detail";
    private static final String API_PATH = "product/product_detail";

    public void fetchProductList(int productId) {
        try {
            // Load third-party credentials
            ThirdParty moogold = thirdPartyRepository.findByNameIgnoreCase("moogold")
                    .orElseThrow(() -> new RuntimeException("MooGold third-party config not found"));

            Map<String, Object> metadata = objectMapper.readValue(moogold.getMetadata(), new TypeReference<>() {});
            String partnerId = (String) metadata.get("partnerId");
            String secretKey = (String) metadata.get("secretKey");

            long timestamp = MoogoldUtil.getCurrentTimestamp();

            // Prepare payload
            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("path", API_PATH);
            payloadMap.put("product_id", productId);
            String payloadJson = objectMapper.writeValueAsString(payloadMap); // This must be used as-is for signing

            // Generate headers
            String basicAuth = MoogoldUtil.generateBasicAuth(partnerId, secretKey);
            String authSignature = MoogoldUtil.generateAuthSignature(payloadJson, timestamp, API_PATH, secretKey);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Basic " + basicAuth);
            headers.set("timestamp", String.valueOf(timestamp));
            headers.set("auth", authSignature);

            HttpEntity<String> entity = new HttpEntity<>(payloadJson, headers);

            log.info("➡️ Sending MooGold product list request with category {}", productId);
            ResponseEntity<String> response = restTemplate.exchange(
                    GET_PRODUCTS_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            log.info("✅ MooGold product list response: {}", response.getBody());
            Object json = objectMapper.readValue(response.getBody(), Object.class);
            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
            System.out.println("Pretty MooGold JSON:\n" + prettyJson);
//            return response.getBody();

        } catch (Exception e) {
            log.error("❌ Failed to fetch MooGold product list", e);
            throw new RuntimeException("Failed to fetch MooGold product list", e);
        }
    }
}
