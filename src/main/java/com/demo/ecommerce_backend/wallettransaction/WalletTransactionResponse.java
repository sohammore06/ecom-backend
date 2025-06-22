package com.demo.ecommerce_backend.wallettransaction;

import com.demo.ecommerce_backend.wallettransaction.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class WalletTransactionResponse {
    private Integer transactionId;
    private TransactionType type;
    private BigDecimal amount;
    private String description;
    private LocalDateTime timestamp;
}
