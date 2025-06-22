package com.demo.ecommerce_backend.auth;

import com.demo.ecommerce_backend.User.UserResponse;
import com.demo.ecommerce_backend.wallet.WalletResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    private UserResponse user;
    private BigDecimal walletBalance;
}
