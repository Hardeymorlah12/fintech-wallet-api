package com.hardeymorlah.walletapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
    @Table(name = "audit_logs")
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class AuditLog {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private Long adminUserId;

        private String action;

        private String target;

        private LocalDateTime createdAt;
    }
