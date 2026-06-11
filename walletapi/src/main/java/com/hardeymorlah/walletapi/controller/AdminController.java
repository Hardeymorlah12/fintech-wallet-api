package com.hardeymorlah.walletapi.controller;

import com.hardeymorlah.walletapi.dto.ApiResponse;
import com.hardeymorlah.walletapi.dto.AuditLogPageResponse;
import com.hardeymorlah.walletapi.dto.UserPageResponse;
import com.hardeymorlah.walletapi.dto.WalletPageResponse;
import com.hardeymorlah.walletapi.entity.TransactionPageResponse;
import com.hardeymorlah.walletapi.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PatchMapping("/wallets/{walletId}/freeze")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> freezeWallet(
            @PathVariable Long walletId
    ) {

        adminService.freezeWallet(walletId);

        ApiResponse<?> response = ApiResponse.builder()
                .success(true)
                .message("Wallet frozen successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }


    @PatchMapping("/wallets/{walletId}/unfreeze")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> unfreezeWallet(
            @PathVariable Long walletId
    ) {

        adminService.unfreezeWallet(walletId);

        ApiResponse<?> response = ApiResponse.builder()
                .success(true)
                .message("Wallet unfrozen successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/{userId}/promote")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> promoteUser(
            @PathVariable Long userId
    ) {

        adminService.promoteUser(userId);

        ApiResponse<?> response = ApiResponse.builder()
                .success(true)
                .message("User promoted to ADMIN successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/{userId}/demote")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> demoteUser(
            @PathVariable Long userId
    ) {

        adminService.demoteUser(userId);

        ApiResponse<?> response = ApiResponse.builder()
                .success(true)
                .message("User demoted to USER successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        UserPageResponse responseData =
                adminService.getAllUsers(page, size);

        ApiResponse<?> response = ApiResponse.builder()
                .success(true)
                .message("Users fetched successfully")
                .data(responseData)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/wallets")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> getAllWallets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        WalletPageResponse walletPage =
                adminService.getAllWallets(page, size);

        ApiResponse<?> response = ApiResponse.builder()
                .success(true)
                .message("Wallets fetched successfully")
                .data(walletPage)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        TransactionPageResponse transactions =
                adminService.getAllTransactions(
                        page,
                        size
                );

        ApiResponse<?> response = ApiResponse.builder()
                .success(true)
                .message("Transactions fetched successfully")
                .data(transactions)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        AuditLogPageResponse auditLogs =
                adminService.getAuditLogs(page, size);

        ApiResponse<?> response = ApiResponse.builder()
                .success(true)
                .message("Audit logs fetched successfully")
                .data(auditLogs)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/transactions/{reference}/reverse")
    public ResponseEntity<ApiResponse<?>> reverseTransaction(
            @PathVariable String reference
    ) {

        adminService.reverseTransfer(reference);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Transaction reversed successfully")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
