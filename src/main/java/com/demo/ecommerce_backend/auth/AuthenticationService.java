package com.demo.ecommerce_backend.auth;

import com.demo.ecommerce_backend.User.UserReposirtory;
import com.demo.ecommerce_backend.User.UserResponse;
import com.demo.ecommerce_backend.config.JwtService;
import com.demo.ecommerce_backend.token.TokenRepository;
import com.demo.ecommerce_backend.token.TokenType;
import com.demo.ecommerce_backend.util.ApiResponse;
import com.demo.ecommerce_backend.wallet.Wallet;
import com.demo.ecommerce_backend.wallet.WalletRepository;
import com.demo.ecommerce_backend.wallet.WalletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.demo.ecommerce_backend.User.User;
import com.demo.ecommerce_backend.token.Token;

import java.io.IOException;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserReposirtory repository;
    private final TokenRepository tokenRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public ApiResponse<AuthenticationResponse>  authenticate(AuthenticationRequest request) {
        log.info("authenticating for request");
        final String identifier = (request.getEmail() != null) ? request.getEmail() : request.getPhoneNo();
        System.out.println("Attempting authentication with identifier: " + identifier);

        try {
            // Attempt authentication
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            identifier,
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            log.error("authentication failed:"+ e.getMessage());
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
        var user = repository.findByEmail(identifier)
                .orElseGet(() -> repository.findByPhoneNo(identifier)
                        .orElseThrow(() -> new RuntimeException("User not found with email/phone number: " + identifier)));
        var jwtToken = jwtService.generateToken(user.getUsername());
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
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
        Wallet wallet = walletRepository.findByUser(user)
                .orElseGet(() -> walletRepository.save(Wallet.builder()
                        .user(user)
                        .balance(BigDecimal.ZERO)
                        .build()));

        WalletResponse walletResponse = WalletResponse.builder()
                .walletId(wallet.getId())
                .balance(wallet.getBalance())
                .build();
        BigDecimal walletBalance = wallet.getBalance();
        AuthenticationResponse loginResponse = AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .user(userResponse)
                .walletBalance(walletBalance)
                .build();
        return new ApiResponse<>(true, "Login successful", loginResponse);
    }

    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .active(true)
                .phoneNo(request.getPhoneNo())
                .build();
        if (repository.existsByPhoneNo(request.getPhoneNo())) {
            throw new IllegalStateException("Email or phone number already exists");
        }
        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(user.getUsername());
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUserName(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user.getUsername());
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
