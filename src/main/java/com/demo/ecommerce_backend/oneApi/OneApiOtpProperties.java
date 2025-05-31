package com.demo.ecommerce_backend.oneApi;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "oneapi")
@Data
public class OneApiOtpProperties {
    private String apiKey;
    private String brandName;
}
