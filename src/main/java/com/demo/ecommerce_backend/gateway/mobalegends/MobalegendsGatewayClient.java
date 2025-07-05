package com.demo.ecommerce_backend.gateway.mobalegends;

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

import java.math.BigDecimal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MobalegendsGatewayClient implements GatewayClient<MobalegendsGatewayRequest> { // add this when need to do changes
    private final RestTemplate restTemplate = new RestTemplate();
//    private final RestTemplate restTemplate;

    @Override
    public Map<String, Object> initiatePayment(Merchant merchant, MobalegendsGatewayRequest request) {
        try {
            request.setApiKey(merchant.getApiKey());
            request.setRedirectUrl(merchant.getRedirectUrl());
            request.setUpiId(merchant.getUpiId());
            request.setMerchantName("GameHub");

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
        System.out.println("here is your url check status : "+url);
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            System.out.println("here is your respnose check status : "+response);
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


//{
//        "amount": 1000,
//        "merchantName": "Your Store",
//        "upiId": "yourstore@upi",
//        "customerName": "John Doe",
//        "customerEmail": "john@example.com",
//        "customerMobile": "9876543210",
//        "redirectUrl": "https://yourstore.com/payment-callback",
//        "pInfo": "Order #1234",
//        "udf1": "custom1",
//        "udf2": "custom2",
//        "udf3": "custom3"
//        }

//{
//        "success": true,
//        "transactionId": "TXN16783456789123456",
//        "paymentUrl": "https://gateway.mobalegends.in/pay/TXN16783456789123456",
//        "amount": 1000,
//        "status": "CREATED"
//        }


//{
//        "success": true,
//        "transactionId": "TXN16783456789123456",
//        "amount": 1000,
//        "status": "SUCCESS",
//        "createdAt": "2025-03-02T14:30:45.123Z",
//        "updatedAt": "2025-03-02T14:32:15.456Z",
//        "customerName": "John Doe",
//        "customerEmail": "john@example.com",
//        "customerMobile": "9876543210",
//        "pInfo": "Order #1234"
//        }