package com.hardeymorlah.walletapi.controller;

import com.hardeymorlah.walletapi.dto.ApiResponse;
import com.hardeymorlah.walletapi.dto.PaginatedResponse;
import com.hardeymorlah.walletapi.dto.TransactionResponse;
import com.hardeymorlah.walletapi.entity.Transaction;
import com.hardeymorlah.walletapi.entity.User;
import com.hardeymorlah.walletapi.entity.Wallet;
import com.hardeymorlah.walletapi.repository.TransactionRepository;
import com.hardeymorlah.walletapi.repository.UserRepository;
import com.hardeymorlah.walletapi.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PaginatedResponse<TransactionResponse>>> getMyTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<Transaction> transactionPage =
                transactionRepository.findByWalletId(wallet.getId(), pageable);

        List<TransactionResponse> transactions =
                transactionPage.getContent()
                        .stream()
                        .map(transaction -> TransactionResponse.builder()
                                .id(transaction.getId())
                                .reference(transaction.getReference())
                                .type(transaction.getType())
                                .amount(transaction.getAmount())
                                .createdAt(transaction.getCreatedAt())
                                .build())
                        .toList();

        PaginatedResponse<TransactionResponse> paginatedResponse =
                PaginatedResponse.<TransactionResponse>builder()
                        .transactions(transactions)
                        .currentPage(transactionPage.getNumber())
                        .totalPages(transactionPage.getTotalPages())
                        .totalItems(transactionPage.getTotalElements())
                        .build();

        ApiResponse<PaginatedResponse<TransactionResponse>> response =
                ApiResponse.<PaginatedResponse<TransactionResponse>>builder()
                        .success(true)
                        .message("Transactions fetched successfully")
                        .data(paginatedResponse)
                        .timestamp(LocalDateTime.now())
                        .build();

        return ResponseEntity.ok(response);
    }}