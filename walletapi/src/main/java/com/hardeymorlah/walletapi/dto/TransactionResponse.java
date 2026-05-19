package com.hardeymorlah.walletapi.dto;

import com.hardeymorlah.walletapi.entity.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


    @Data
    @Builder
public class TransactionResponse {

        private Long id;

        private String reference;

        private TransactionType type;

        private BigDecimal amount;

        private LocalDateTime createdAt;
    }

