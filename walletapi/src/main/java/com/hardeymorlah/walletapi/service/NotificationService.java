package com.hardeymorlah.walletapi.service;

import com.hardeymorlah.walletapi.dto.NotificationResponse;
import org.springframework.data.domain.Page;

public interface NotificationService {

    void createNotification(
            Long userId,
            String title,
            String message
    );

    Page<NotificationResponse> getMyNotifications(
            int page,
            int size
    );

    void markAsRead(Long notificationId);
}
