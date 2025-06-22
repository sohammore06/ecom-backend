package com.demo.ecommerce_backend.wallet;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletRequest {
    private BigDecimal amount;
}
