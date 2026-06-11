package com.hardeymorlah.walletapi.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

    @Data
    @Builder
    public class AuditLogPageResponse {

        private long totalItems;

        private int totalPages;

        private int currentPage;

        private List<AuditLogResponse> auditLogs;
    }



