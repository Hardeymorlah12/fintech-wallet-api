package com.hardeymorlah.walletapi.controller;

import com.hardeymorlah.walletapi.dto.ApiResponse;
import com.hardeymorlah.walletapi.dto.NotificationResponse;
import com.hardeymorlah.walletapi.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {

        Page<NotificationResponse> notifications =
                notificationService.getMyNotifications(page, size);

        Map<String, Object> responseData = new HashMap<>();

        responseData.put(
                "notifications",
                notifications.getContent()
        );

        responseData.put(
                "currentPage",
                notifications.getNumber()
        );

        responseData.put(
                "totalItems",
                notifications.getTotalElements()
        );

        responseData.put(
                "totalPages",
                notifications.getTotalPages()
        );

        ApiResponse<?> response = ApiResponse.builder()
                .success(true)
                .message("Notifications fetched successfully")
                .data(responseData)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<?>> markAsRead(
            @PathVariable Long notificationId
    ) {

        notificationService.markAsRead(notificationId);

        ApiResponse<?> response = ApiResponse.builder()
                .success(true)
                .message("Notification marked as read")
                .data(notificationId)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }
}

