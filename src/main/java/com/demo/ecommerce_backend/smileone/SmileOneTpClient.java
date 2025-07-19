package com.demo.ecommerce_backend.smileone;

import com.demo.ecommerce_backend.thirdparty.ThirdParty;
import com.demo.ecommerce_backend.thirdparty.ThirdPartyRepository;
import com.demo.ecommerce_backend.util.SmileOneUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SmileOneTpClient {
    private static final Logger log = LoggerFactory.getLogger(SmileOneTpClient.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    private final ThirdPartyRepository thirdPartyRepository;
    private static final String GET_PRODUCTS_API_URL = "https://www.smile.one/smilecoin/api/productlist";
    private static final String CREATE_ORDER_API_URL="https://www.smile.one/smilecoin/api/createorder";
//    public SmileOneTpClient(ObjectMapper objectMapper) {
//        this.objectMapper = objectMapper;
//    }

    public SmileOneProductListResponse fetchProductList(ThirdParty smileOneConfig,String productName) {
        try {
            // Parse metadata
            log.info("➡️ Fetching SmileOne products"+smileOneConfig);
            Map<String, Object> metadata = objectMapper.readValue(
                    smileOneConfig.getMetadata(),
                    new TypeReference<Map<String, Object>>() {}
            );


            String uid = (String) metadata.get("uid");
            String email = (String) metadata.get("email");
            String key = (String) metadata.get("key");
            long timestamp = Instant.now().getEpochSecond();

            // Prepare parameters
            Map<String, String> params = new HashMap<>();
            params.put("uid", uid);
            params.put("email", email);
            params.put("product", productName);
            params.put("time", String.valueOf(timestamp));

            // Generate sign
            String sign = SmileOneUtil.generateSignature(params, key);

            // Convert to form-data map
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            params.forEach(body::add);
            body.add("sign", sign);
            System.out.println("calling smile one sync api");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);


            ResponseEntity<SmileOneProductListResponse> response = restTemplate.postForEntity(
                    GET_PRODUCTS_API_URL,
                    entity,
                    SmileOneProductListResponse.class
            );
            log.info("➡️ Smile one sent response--->"+ response.getBody());
            System.out.println("here is your output--->"+response.getBody());
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch SmileOne product list", e);
        }
    }
    @Transactional
    public SmileOneOrderResponse createOrder(SmileOneOrderRequest request) {
        try {
            ThirdParty config = thirdPartyRepository.findByNameIgnoreCase("smileone")
                    .orElseThrow(() -> new RuntimeException("SmileOne third-party config not found"));

            Map<String, Object> metadata = objectMapper.readValue(
                    config.getMetadata(), new TypeReference<>() {}
            );

            String uid = (String) metadata.get("uid");
            String email = (String) metadata.get("email");
            String key = (String) metadata.get("key");
//            String region = (String) metadata.getOrDefault("region", "br");
            long timestamp = Instant.now().getEpochSecond();

            // Prepare request params
            Map<String, String> params = new HashMap<>();
            params.put("uid", uid);
            params.put("email", email);
            params.put("userid", request.getUserId());
            params.put("zoneid", request.getZoneId());
            params.put("product", "mobilelegends");
            params.put("productid", request.getProductId());
            params.put("time", String.valueOf(timestamp));

            String sign = SmileOneUtil.generateSignature(params, key);

            // Build request body
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            params.forEach(body::add);
            body.add("sign", sign);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
            log.info("SmileOne createOrder request: {}", entity);
            System.out.println(entity);
            ResponseEntity<SmileOneOrderResponse> response= restTemplate.postForEntity(CREATE_ORDER_API_URL, entity, SmileOneOrderResponse.class);
            System.out.println("here is your response from smile one for product--->"+response);
            log.info("SmileOne createOrder response: {}",response);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to create SmileOne order", e);
            throw new RuntimeException("Failed to create SmileOne order", e);
        }
    }
}
