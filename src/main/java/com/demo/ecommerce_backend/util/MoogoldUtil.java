package com.demo.ecommerce_backend.util;

import lombok.experimental.UtilityClass;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@UtilityClass
public class MoogoldUtil {

    /**
     * Generates Base64 encoded Basic Auth header value from partnerId and secretKey.
     */
    public String generateBasicAuth(String partnerId, String secretKey) {
        String authString = partnerId + ":" + secretKey;
        return Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Returns current UNIX timestamp in seconds.
     */
    public long getCurrentTimestamp() {
        return Instant.now().getEpochSecond();
    }

    /**
     * Generates HMAC SHA256 authentication signature.
     *
     * @param payloadJson The raw JSON body you're going to send (as a String).
     * @param timestamp   The timestamp in UNIX seconds.
     * @param path        The path portion of the endpoint, like "order/create_order".
     * @param secretKey   Your MooGold secret key.
     * @return HMAC SHA256 hex-encoded string.
     */
    public String generateAuthSignature(String payloadJson, long timestamp, String path, String secretKey) {
        try {
            String stringToSign = payloadJson + timestamp + path;
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secretKeySpec);
            byte[] hash = sha256_HMAC.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error generating MooGold auth signature", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
