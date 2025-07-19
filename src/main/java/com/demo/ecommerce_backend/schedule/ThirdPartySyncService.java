package com.demo.ecommerce_backend.schedule;
import com.demo.ecommerce_backend.product.ProductSource;
import com.demo.ecommerce_backend.smileone.SmileOneSyncService;
import com.demo.ecommerce_backend.thirdparty.ThirdParty;
import com.demo.ecommerce_backend.thirdparty.ThirdPartyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThirdPartySyncService {
    private final ThirdPartyRepository thirdPartyRepository;
    private final SmileOneSyncService smileOneSyncService;
    // private final MooGoldSyncService mooGoldSyncService; // (optional to be added later)

    // Scheduled daily at 6 AM
    @Scheduled(cron = "0 0 6 * * *")
    public void runScheduledSync() {
        log.info("🔁 Starting daily third-party product sync");
        runSync();
    }

    public void runSync() {
        List<ThirdParty> thirdParties = thirdPartyRepository.findAll();
        System.out.println("we are inside run synccc--->");
        for (ThirdParty thirdParty : thirdParties) {
            if (!thirdParty.isActive()) continue;

            ProductSource source;
            try {
                source = ProductSource.valueOf(thirdParty.getName().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("⚠️ Unknown third-party source: {}", thirdParty.getName());
                continue;
            }

            switch (source) {
                case SMILEONE -> smileOneSyncService.syncProducts();
                // case MOOGOLD -> mooGoldSyncService.syncProducts(thirdParty);
                default -> log.warn("❌ Unsupported third-party: {}", source);
            }
        }
    }
}
