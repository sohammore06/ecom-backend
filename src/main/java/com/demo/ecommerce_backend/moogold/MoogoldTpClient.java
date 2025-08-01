package com.demo.ecommerce_backend.moogold;

import com.demo.ecommerce_backend.thirdparty.ThirdParty;
import com.demo.ecommerce_backend.thirdparty.ThirdPartyRepository;
import com.demo.ecommerce_backend.util.MoogoldUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
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

    public MoogoldProductListResponse fetchProductList(int productId) {
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
            ResponseEntity<MoogoldProductListResponse> response = restTemplate.exchange(
                    GET_PRODUCTS_URL,
                    HttpMethod.POST,
                    entity,
                    MoogoldProductListResponse.class
            );

            log.info("✅ MooGold product list response: {}", response.getBody());
            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(response.getBody());
            System.out.println("Pretty MooGold JSON:\n" + prettyJson);

            return response.getBody();

        } catch (Exception e) {
            log.error("❌ Failed to fetch MooGold product list", e);
            throw new RuntimeException("Failed to fetch MooGold product list", e);
        }
    }
    public Map<String, String> fetchServerList(int productId) {
        try {
            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("path", "product/server_list");
            payloadMap.put("product_id", productId);

            String payloadJson = objectMapper.writeValueAsString(payloadMap);

            // Auth setup (same as before)
            ThirdParty moogold = thirdPartyRepository.findByNameIgnoreCase("moogold")
                    .orElseThrow(() -> new RuntimeException("MooGold third-party config not found"));
            Map<String, Object> metadata = objectMapper.readValue(moogold.getMetadata(), new TypeReference<>() {});
            String partnerId = (String) metadata.get("partnerId");
            String secretKey = (String) metadata.get("secretKey");

            long timestamp = MoogoldUtil.getCurrentTimestamp();
            String basicAuth = MoogoldUtil.generateBasicAuth(partnerId, secretKey);
            String authSignature = MoogoldUtil.generateAuthSignature(payloadJson, timestamp, "product/server_list", secretKey);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Basic " + basicAuth);
            headers.set("timestamp", String.valueOf(timestamp));
            headers.set("auth", authSignature);

            HttpEntity<String> entity = new HttpEntity<>(payloadJson, headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    "https://moogold.com/wp-json/v1/api/product/server_list",
                    HttpMethod.POST,
                    entity,
                    JsonNode.class
            );
            log.info("🌐 Raw server list response for product_id {} => {}", productId, response.getBody());
            System.out.println("🌐 Raw server list response for product_id " + productId + " => " + response.getBody());

            JsonNode body = response.getBody();
            Map<String, String> serverMap = new HashMap<>();
            if (body != null) {
                body.fields().forEachRemaining(entry -> serverMap.put(entry.getKey(), entry.getValue().asText()));
            }
            log.info("📦 MooGold server list for product_id {} => {}", productId, serverMap);
            System.out.println("📦 Server list for product_id " + productId + " => " + serverMap);
            return serverMap;

        } catch (Exception e) {
            log.error("❌ Failed to fetch MooGold server list", e);
            return new HashMap<>();
        }
    }

    public JsonNode createOrder(Map<String, String> data, String partnerOrderId) {
        try {
            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("path", "order/create_order");
            payloadMap.put("data", data);
            if (partnerOrderId != null) {
                payloadMap.put("partnerOrderId", partnerOrderId);
            }

            String payloadJson = objectMapper.writeValueAsString(payloadMap);

            // Auth
            ThirdParty moogold = thirdPartyRepository.findByNameIgnoreCase("moogold")
                    .orElseThrow(() -> new RuntimeException("MooGold config not found"));
            Map<String, Object> metadata = objectMapper.readValue(moogold.getMetadata(), new TypeReference<>() {});
            String partnerId = (String) metadata.get("partnerId");
            String secretKey = (String) metadata.get("secretKey");

            long timestamp = MoogoldUtil.getCurrentTimestamp();
            String basicAuth = MoogoldUtil.generateBasicAuth(partnerId, secretKey);
            String authSignature = MoogoldUtil.generateAuthSignature(payloadJson, timestamp, "order/create_order", secretKey);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Basic " + basicAuth);
            headers.set("timestamp", String.valueOf(timestamp));
            headers.set("auth", authSignature);

            HttpEntity<String> entity = new HttpEntity<>(payloadJson, headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    "https://moogold.com/wp-json/v1/api/order/create_order",
                    HttpMethod.POST,
                    entity,
                    JsonNode.class
            );

            log.info("✅ MooGold order creation response: {}", response.getBody());
            return response.getBody();

        } catch (Exception e) {
            log.error("❌ Failed to create MooGold order", e);
            throw new RuntimeException("MooGold order creation failed", e);
        }
    }


}
