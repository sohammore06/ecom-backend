package com.demo.ecommerce_backend.gateway;

import com.demo.ecommerce_backend.gateway.mobalegends.MobalegendsGatewayClient;
import com.demo.ecommerce_backend.gateway.upigateway.UpiGatewayClient;
import com.demo.ecommerce_backend.merchant.Merchant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GatewayFactory {

    private final MobalegendsGatewayClient mobalegendsClient;
    private final UpiGatewayClient upiGatewayClient;
    public GatewayClient getGatewayClient(Merchant merchant) {
        switch (merchant.getName().toLowerCase()) {
            case "mobalegends":
                return mobalegendsClient;
            case "upigateway":
                return upiGatewayClient;
            default:
                throw new UnsupportedOperationException("Unsupported merchant: " + merchant.getName());
        }
    }
}
