package com.demo.ecommerce_backend.gateway.upigateway;

import com.demo.ecommerce_backend.exception.PaymentException;
import com.demo.ecommerce_backend.gateway.GatewayClient;
import com.demo.ecommerce_backend.gateway.GatewayStatusResponse;
import com.demo.ecommerce_backend.merchant.Merchant;
import com.demo.ecommerce_backend.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
@Component
@RequiredArgsConstructor
public class UpiGatewayClient implements GatewayClient<UpiGatewayRequest> {
    private final RestTemplate restTemplate = new RestTemplate();
//    private final RestTemplate restTemplate;

    @Override
    public Map<String, Object> initiatePayment(Merchant merchant, UpiGatewayRequest request) {
        try {
            request.setKey(merchant.getApiKey());
            request.setRedirectUrl(merchant.getRedirectUrl());

            return restTemplate.postForObject(
                    merchant.getPaymentCreateUrl(),
                    request,
                    Map.class
            );

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw new PaymentException("Payment gateway error: " + ex.getResponseBodyAsString(), ex);
        } catch (Exception e) {
            throw new PaymentException("Unexpected error while calling payment gateway: " + e.getMessage(), e);
        }
    }
    @Override
    public GatewayStatusResponse checkStatus(Merchant merchant, String txnId) {
        String url = merchant.getPaymentStatusUrl() + "/" + txnId;

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            Map<String, Object> raw = response;


            String status = ((String) response.get("status")).toUpperCase();

            return GatewayStatusResponse.builder()
                    .transactionId(txnId)
                    .status(status)
                    .rawResponse(raw)
                    .build();
        } catch (Exception e) {
            throw new PaymentException("Failed to check Mobalegends status: " + e.getMessage(), e);
        }
    }

}
