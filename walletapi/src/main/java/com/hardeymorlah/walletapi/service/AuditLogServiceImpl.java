package com.hardeymorlah.walletapi.service;

import com.hardeymorlah.walletapi.entity.AuditLog;
import com.hardeymorlah.walletapi.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
    @RequiredArgsConstructor
    public class AuditLogServiceImpl implements AuditLogService {

        private final AuditLogRepository auditLogRepository;

        @Override
        public void logAction(
                Long adminUserId,
                String action,
                String target
        ) {

            AuditLog auditLog = AuditLog.builder()
                    .adminUserId(adminUserId)
                    .action(action)
                    .target(target)
                    .createdAt(LocalDateTime.now())
                    .build();

            auditLogRepository.save(auditLog);
        }
    }

