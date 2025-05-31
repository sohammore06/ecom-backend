package com.demo.ecommerce_backend.otp;
import com.demo.ecommerce_backend.oneApi.OneApiOtpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService{
    private final OneApiOtpClient oneApiOtpClient;
    private final StringRedisTemplate redisTemplate;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger log = LoggerFactory.getLogger(OneApiOtpClient.class);
    public boolean sendOtp(OtpRequest request) {
        String otp = generateOtp();
        boolean sent = oneApiOtpClient.sendOtp(request.getCustomerName(), request.getNumber(), otp);


        if (sent) {
            log.info("here is your sent--->"+sent);
            String key = buildOtpKey(request.getNumber());
            log.info("setting key"+key);
            redisTemplate.opsForValue().set(key, otp, Duration.ofMinutes(5)); // TTL of 5 minutes
        }

        return sent;
    }
    public boolean verifyOtp(String phoneNumber, String otp) {
        String key = "OTP_" + phoneNumber;
        String cachedOtp = redisTemplate.opsForValue().get(key);
        return otp.equals(cachedOtp);
    }

    private String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    private String buildOtpKey(String phoneNo) {
        return "OTP_" + phoneNo;
    }
}
