package com.hardeymorlah.walletapi.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

    @Entity
    @Table(name = "transactions")
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class Transaction {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String reference;

        @Enumerated(EnumType.STRING)
        private TransactionType type;

        private BigDecimal amount;

        private Long walletId;

        private LocalDateTime createdAt;
    }

