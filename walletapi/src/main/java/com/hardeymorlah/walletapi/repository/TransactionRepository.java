package com.hardeymorlah.walletapi.repository;


import com.hardeymorlah.walletapi.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByWalletId(Long walletId, Pageable pageable);

    List<Transaction> findByWalletId(Long walletId);

    List<Transaction> findByGroupReference(String groupReference);
    List<Transaction> findByWalletIdAndCreatedAtBetween(
            Long walletId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}
