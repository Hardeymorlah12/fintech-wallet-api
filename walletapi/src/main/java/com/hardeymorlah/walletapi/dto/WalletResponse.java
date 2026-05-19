package com.hardeymorlah.walletapi.dto;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

    @Data
    @Builder
    public class WalletResponse {

        private Long id;

        private UserResponse user;

        private BigDecimal balance;

        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;
    }

