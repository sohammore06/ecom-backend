package com.demo.ecommerce_backend.moogold;
import com.demo.ecommerce_backend.thirdparty.ThirdParty;
import com.demo.ecommerce_backend.thirdparty.ThirdPartyRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoogoldSyncService {
    private final MoogoldTpClient moogoldTpClient;
    private final ThirdPartyRepository thirdPartyRepository;
    private final ObjectMapper objectMapper;

    public void syncProducts(ThirdParty moogoldConfig) {
        log.info("➡️ Starting MooGold product sync for category ID 1");

        try {
            moogoldTpClient.fetchProductList(1);
            log.info("✅ MooGold raw product response:\n{}");

        }catch (Exception e) {
            log.error("❌ Failed to sync MooGold products", e);
        }
    }
}
