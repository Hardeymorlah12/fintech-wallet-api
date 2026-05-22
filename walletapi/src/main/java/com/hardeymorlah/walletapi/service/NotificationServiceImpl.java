package com.hardeymorlah.walletapi.service;

import com.hardeymorlah.walletapi.dto.NotificationResponse;
import com.hardeymorlah.walletapi.entity.Notification;
import com.hardeymorlah.walletapi.entity.User;
import com.hardeymorlah.walletapi.exception.NotificationNotFoundException;
import com.hardeymorlah.walletapi.repository.NotificationRepository;
import com.hardeymorlah.walletapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

    @Service
    @RequiredArgsConstructor
    public class NotificationServiceImpl implements NotificationService {

        private final NotificationRepository notificationRepository;
        private final UserRepository userRepository;

        @Override
        public void createNotification(
                Long userId,
                String title,
                String message
        ) {

            User user = userRepository.findById(userId)
                    .orElseThrow(() ->
                            new NotificationNotFoundException(
                                    "Notification not found"
                            ));

            Notification notification = Notification.builder()
                    .title(title)
                    .message(message)
                    .isRead(false)
                    .user(user)
                    .build();

            notificationRepository.save(notification);
        }

        @Override
        public Page<NotificationResponse> getMyNotifications(
                int page,
                int size
        ) {

            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            String email = authentication.getName();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() ->
                            new RuntimeException("User not found"));

            Pageable pageable =
                    PageRequest.of(page, size);

            return notificationRepository
                    .findByUserOrderByCreatedAtDesc(user, pageable)
                    .map(this::mapToResponse);
        }

        @Override
        public void markAsRead(Long notificationId) {

            Notification notification =
                    notificationRepository.findById(notificationId)
                            .orElseThrow(() ->
                                    new NotificationNotFoundException(
                                            "Notification not found"
                                    ));

            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            String email = authentication.getName();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() ->
                            new RuntimeException("User not found"));

            // ownership check
            if (!notification.getUser().getId().equals(user.getId())) {
                throw new RuntimeException(
                        "Unauthorized access to notification"
                );
            }

            notification.setRead(true);

            notificationRepository.save(notification);
        }

        private NotificationResponse mapToResponse(
                Notification notification
        ) {

            return NotificationResponse.builder()
                    .id(notification.getId())
                    .title(notification.getTitle())
                    .message(notification.getMessage())
                    .isRead(notification.isRead())
                    .createdAt(notification.getCreatedAt())
                    .build();
        }
    }

