package com.hardeymorlah.walletapi.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
    public class AuditLogResponse {

        private Long id;

        private Long adminUserId;

        private String action;

        private String target;

        private LocalDateTime createdAt;
    }


