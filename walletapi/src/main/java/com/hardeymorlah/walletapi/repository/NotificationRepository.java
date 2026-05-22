package com.hardeymorlah.walletapi.repository;

import com.hardeymorlah.walletapi.entity.Notification;
import com.hardeymorlah.walletapi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserOrderByCreatedAtDesc(
            User user,
            Pageable pageable
    );
}

