package com.demo.ecommerce_backend.thirdparty;
import com.demo.ecommerce_backend.config.OneApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class OneApiOtpClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private final OneApiProperties oneApiProperties;

    private static final String API_URL = "https://backend.oneapi.in/sms/sendotp";

    public boolean sendOtp(String customerName, String phoneNumber, String otp) {
        String json = String.format("""
            {
                "apiKey": "%s",
                "brandName": "%s",
                "customerName": "%s",
                "number": %s,
                "otp": %s
            }
        """, oneApiProperties.getApiKey(), oneApiProperties.getBrandName(), customerName, phoneNumber, otp);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(json, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, request, String.class);
            log.info("OneAPI OTP response: {}", response.getBody());
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            log.error("Error sending OTP via OneAPI", e);
            return false;
        }
    }
}
