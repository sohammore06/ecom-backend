package com.demo.ecommerce_backend.wallettransaction;

import com.demo.ecommerce_backend.wallettransaction.TransactionType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletTransactionRequest {
    private BigDecimal amount;
    private String description;
    private TransactionType type;
}
