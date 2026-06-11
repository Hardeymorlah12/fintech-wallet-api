package com.hardeymorlah.walletapi.dto;

import com.hardeymorlah.walletapi.entity.TransactionStatus;
import com.hardeymorlah.walletapi.entity.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {

    private Long id;

    private Long walletId;

    private BigDecimal amount;

    private TransactionType type;

    private TransactionStatus status;

    private String groupReference;

    private String reference;

    private LocalDateTime createdAt;
}