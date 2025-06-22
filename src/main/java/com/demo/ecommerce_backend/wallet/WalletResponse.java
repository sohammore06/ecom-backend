package com.demo.ecommerce_backend.wallet;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WalletResponse {
    private Integer walletId;
    private BigDecimal balance;
}
