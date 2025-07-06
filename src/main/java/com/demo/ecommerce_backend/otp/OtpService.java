package com.demo.ecommerce_backend.otp;
import com.demo.ecommerce_backend.User.User;
import com.demo.ecommerce_backend.User.UserReposirtory;
import com.demo.ecommerce_backend.User.UserResponse;
import com.demo.ecommerce_backend.auth.AuthenticationResponse;
import com.demo.ecommerce_backend.config.JwtService;
import com.demo.ecommerce_backend.oneApi.OneApiOtpClient;
import com.demo.ecommerce_backend.wallet.Wallet;
import com.demo.ecommerce_backend.wallet.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService{
    private final OneApiOtpClient oneApiOtpClient;
    private final StringRedisTemplate redisTemplate;
    private final RestTemplate restTemplate = new RestTemplate();
    private final UserReposirtory userRepository;
    private final WalletRepository walletRepository;
    private final JwtService jwtService;
    private static final int MAX_ATTEMPTS = 4;
    private static final long COOLDOWN_SECONDS = 300;

    private static final Logger log = LoggerFactory.getLogger(OneApiOtpClient.class);
    public OtpResponse sendOtp(OtpRequest request) {
        String phone = request.getNumber();
        String flow = request.getFlow();

        String otpKey = buildOtpKey(phone);
        String sendAttemptKey = buildSendAttemptKey(phone);
        String cooldownKey = buildCooldownKey(phone);
        String verifyAttemptKey = buildAttemptKey(phone); // for cleanup
        System.out.println("here is your cooldown key--->"+redisTemplate.hasKey(cooldownKey));
        // Check cooldown
        if (Boolean.TRUE.equals(redisTemplate.hasKey(cooldownKey))) {
            return new OtpResponse(false, "Too many attempts. Please wait before requesting another OTP.");
        }

        // Increment send attempts
        Long sends = redisTemplate.opsForValue().increment(sendAttemptKey);
        redisTemplate.expire(sendAttemptKey, Duration.ofSeconds(COOLDOWN_SECONDS));

        if (sends != null && sends >= MAX_ATTEMPTS) {
            redisTemplate.opsForValue().set(cooldownKey, "1", Duration.ofSeconds(COOLDOWN_SECONDS));
            return new OtpResponse(false, "OTP request limit reached. Try again after 10 minutes.");
        }

        boolean userExists = userRepository.existsByPhoneNo(phone);

        if ("LOGIN".equalsIgnoreCase(flow)) {
            if (!userExists) {
                return new OtpResponse(false, "User does not exist. Please register.");
            }
        } else if ("REGISTER".equalsIgnoreCase(flow)) {
            if (userExists) {
                return new OtpResponse(false, "User already exists. Please login.");
            }
        } else {
            return new OtpResponse(false, "Invalid flow type.");
        }

        String otp = generateOtp();
        String name = request.getCustomerName();

        if ("LOGIN".equalsIgnoreCase(flow)) {
            name = userRepository.findByPhoneNo(phone)
                    .map(user -> user.getFirstName() + " " + user.getLastName())
                    .orElse("User");
        }

        boolean sent = oneApiOtpClient.sendOtp(name, phone, otp);

        if (sent) {
            redisTemplate.opsForValue().set(otpKey, otp, Duration.ofMinutes(5));
            redisTemplate.delete(verifyAttemptKey); // clear verify attempts on new send
            return new OtpResponse(true, "OTP sent successfully.");
        } else {
            return new OtpResponse(false, "Failed to send OTP. Try again.");
        }
    }


    public AuthenticationResponse loginViaOtp(VerifyOtpRequest request) {
        String phone = request.getPhoneNo();
        String otpKey = buildOtpKey(phone);
        String attemptKey = buildAttemptKey(phone);
        String cooldownKey = buildCooldownKey(phone);
        String sendAttemptKey=buildSendAttemptKey(phone);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(cooldownKey))) {
            throw new RuntimeException("Too many failed attempts. Try again after 10 minutes.");
        }

        String cachedOtp = redisTemplate.opsForValue().get(otpKey);
        if (cachedOtp == null || !cachedOtp.equals(request.getOtp())) {
            incrementFailedAttempt(phone);
            throw new RuntimeException("Invalid OTP.");
        }

        // Clear OTP-related keys
        redisTemplate.delete(otpKey);
        redisTemplate.delete(attemptKey);
        redisTemplate.delete(cooldownKey);
        redisTemplate.delete(sendAttemptKey);
        // Proceed to login
        User user = userRepository.findByPhoneNo(phone)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtService.generateToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user);

        Wallet wallet = walletRepository.findByUser(user)
                .orElseGet(() -> walletRepository.save(Wallet.builder()
                        .user(user)
                        .balance(BigDecimal.ZERO)
                        .build()));

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNo(user.getPhoneNo())
                .role(user.getRole())
                .address(user.getAddress())
                .active(user.getActive())
                .build();

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userResponse)
                .walletBalance(wallet.getBalance())
                .build();
    }

    private void incrementFailedAttempt(String phone) {
        String attemptKey = buildAttemptKey(phone);
        String cooldownKey = buildCooldownKey(phone);
        Long attempts = redisTemplate.opsForValue().increment(attemptKey);

        redisTemplate.expire(attemptKey, Duration.ofSeconds(COOLDOWN_SECONDS));

        if (attempts != null && attempts >= MAX_ATTEMPTS) {
            redisTemplate.opsForValue().set(cooldownKey, "1", Duration.ofSeconds(COOLDOWN_SECONDS));
        }
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
    private String buildAttemptKey(String phone) {
        return "OTP_ATTEMPTS_" + phone;
    }
    private String buildCooldownKey(String phone) {
        return "OTP_COOLDOWN_" + phone;
    }
    private String buildSendAttemptKey(String phone) {
        return "OTP_SEND_ATTEMPTS_" + phone;
    }

}
