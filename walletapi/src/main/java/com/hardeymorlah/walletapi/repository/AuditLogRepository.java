package com.hardeymorlah.walletapi.repository;

import com.hardeymorlah.walletapi.entity.AuditLog;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {

    @Modifying
    @Transactional
    void deleteByCreatedAtBefore(
            LocalDateTime dateTime
    );
}
