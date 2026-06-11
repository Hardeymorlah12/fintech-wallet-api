package com.hardeymorlah.walletapi.repository;

import com.hardeymorlah.walletapi.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {
}
