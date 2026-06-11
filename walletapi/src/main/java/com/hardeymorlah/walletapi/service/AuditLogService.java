package com.hardeymorlah.walletapi.service;

    public interface AuditLogService {

        void logAction(
                Long adminUserId,
                String action,
                String target
        );
    }
