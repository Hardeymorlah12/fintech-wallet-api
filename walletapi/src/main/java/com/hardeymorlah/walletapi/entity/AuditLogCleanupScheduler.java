package com.hardeymorlah.walletapi.entity;


import com.hardeymorlah.walletapi.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
    @RequiredArgsConstructor
    @Slf4j
    public class AuditLogCleanupScheduler {

        private final AuditLogRepository auditLogRepository;

        @Scheduled(cron = "0 0 2 * * *")
        public void cleanupOldLogs() {

            auditLogRepository.deleteByCreatedAtBefore(
                    LocalDateTime.now().minusMonths(6)
            );

            log.info(
                    "Old audit logs cleaned up"
            );
        }
    }

