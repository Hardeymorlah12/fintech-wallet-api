package com.hardeymorlah.walletapi.service;

import com.hardeymorlah.walletapi.dto.*;
import com.hardeymorlah.walletapi.entity.*;
import com.hardeymorlah.walletapi.exception.UserNotFoundException;
import com.hardeymorlah.walletapi.repository.AuditLogRepository;
import com.hardeymorlah.walletapi.repository.TransactionRepository;
import com.hardeymorlah.walletapi.repository.UserRepository;
import com.hardeymorlah.walletapi.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
    @RequiredArgsConstructor
    public class AdminServiceImpl implements AdminService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;
    private final AuditLogRepository auditLogRepository;


    @Override
    public void freezeWallet(Long walletId) {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found"));

        wallet.setFrozen(true);

        walletRepository.save(wallet);

        User admin = getAuthenticatedUser();

        auditLogService.logAction(
                admin.getId(),
                "FREEZE_WALLET",
                "Wallet ID: " + walletId
        );
    }

    @Override
    public void unfreezeWallet(Long walletId) {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found"));

        wallet.setFrozen(false);

        walletRepository.save(wallet);

        walletRepository.save(wallet);

        User admin = getAuthenticatedUser();

        auditLogService.logAction(
                admin.getId(),
                "UNFREEZE_WALLET",
                "Wallet ID: " + walletId
        );
    }

    @Override
    public void promoteUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        user.setRole(Role.ADMIN);

        userRepository.save(user);
        User admin = getAuthenticatedUser();
        auditLogService.logAction(
                admin.getId(),
                "PROMOTE_USER",
                "User ID: " + userId
        );
    }

    @Override
    public void demoteUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        user.setRole(Role.USER);

        userRepository.save(user);

        User admin = getAuthenticatedUser();
        auditLogService.logAction(
                admin.getId(),
                "DEMOTE_USER",
                "User ID: " + userId
        );
    }

        @Override
        public UserPageResponse getAllUsers(int page, int size) {

            Pageable pageable = PageRequest.of(page, size);

            Page<User> userPage =
                    userRepository.findAll(pageable);

            List<UserResponse> users =
                    userPage.getContent()
                            .stream()
                            .map(user -> UserResponse.builder()
                                    .id(user.getId())
                                    .fullName(user.getFullName())
                                    .email(user.getEmail())
                                    .role(user.getRole())
                                    .createdAt(user.getCreatedAt())
                                    .build())
                            .toList();

            return UserPageResponse.builder()
                    .users(users)
                    .currentPage(userPage.getNumber())
                    .totalItems(userPage.getTotalElements())
                    .totalPages(userPage.getTotalPages())
                    .build();
        }

    @Override
    public WalletPageResponse getAllWallets(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Wallet> walletPage =
                walletRepository.findAll(pageable);

        List<WalletResponse> wallets =
                walletPage.getContent()
                        .stream()
                        .map(this::mapToWalletResponse)
                        .toList();

        return WalletPageResponse.builder()
                .wallets(wallets)
                .currentPage(walletPage.getNumber())
                .totalItems(walletPage.getTotalElements())
                .totalPages(walletPage.getTotalPages())
                .build();
    }

    private WalletResponse mapToWalletResponse(Wallet wallet) {

        return getWalletResponse(wallet);
    }

    static WalletResponse getWalletResponse(Wallet wallet) {
        UserResponse userResponse = UserResponse.builder()
                .id(wallet.getUser().getId())
                .fullName(wallet.getUser().getFullName())
                .email(wallet.getUser().getEmail())
                .role(wallet.getUser().getRole())
                .createdAt(wallet.getUser().getCreatedAt())
                .build();

        return WalletResponse.builder()
                .id(wallet.getId())
                .user(userResponse)
                .balance(wallet.getBalance())
                .frozen(wallet.isFrozen())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .build();
    }

    @Override
    public TransactionPageResponse getAllTransactions(
            int page,
            int size
    ) {

        Pageable pageable =
                PageRequest.of(page, size);

        Page<Transaction> transactionPage =
                transactionRepository.findAll(pageable);

        List<TransactionResponse> transactions =
                transactionPage.getContent()
                        .stream()
                        .map(tx -> TransactionResponse.builder()
                                .id(tx.getId())
                                .walletId(tx.getWalletId())
                                .amount(tx.getAmount())
                                .type(tx.getType())
                                .reference(tx.getReference())
                                .createdAt(tx.getCreatedAt())
                                .build())
                        .toList();

        return TransactionPageResponse.builder()
                .transactions(transactions)
                .currentPage(transactionPage.getNumber())
                .totalItems(transactionPage.getTotalElements())
                .totalPages(transactionPage.getTotalPages())
                .build();
    }

    private User getAuthenticatedUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));
    }
    @Override
    public AuditLogPageResponse getAuditLogs(
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        Page<AuditLog> auditLogPage =
                auditLogRepository.findAll(pageable);

        List<AuditLogResponse> auditLogs =
                auditLogPage.getContent()
                        .stream()
                        .map(log -> AuditLogResponse.builder()
                                .id(log.getId())
                                .adminUserId(log.getAdminUserId())
                                .action(log.getAction())
                                .target(log.getTarget())
                                .createdAt(log.getCreatedAt())
                                .build())
                        .toList();

        return AuditLogPageResponse.builder()
                .auditLogs(auditLogs)
                .currentPage(auditLogPage.getNumber())
                .totalItems(auditLogPage.getTotalElements())
                .totalPages(auditLogPage.getTotalPages())
                .build();
    }

    @Transactional
    @Override
    public void reverseTransfer(String groupReference) {

        List<Transaction> transactions =
                transactionRepository.findByGroupReference(groupReference);

        if (transactions.isEmpty()) {
            throw new RuntimeException("Transfer not found");
        }

        boolean alreadyReversed = transactions.stream()
                .allMatch(tx ->
                        tx.getStatus() == TransactionStatus.REVERSED);

        if (alreadyReversed) {
            throw new RuntimeException(
                    "Transfer already reversed"
            );
        }

        Transaction debitTx = transactions.stream()
                .filter(tx ->
                        tx.getType() == TransactionType.DEBIT)
                .findFirst()
                .orElseThrow();

        Transaction creditTx = transactions.stream()
                .filter(tx ->
                        tx.getType() == TransactionType.CREDIT)
                .findFirst()
                .orElseThrow();

        Wallet senderWallet = walletRepository
                .findById(debitTx.getWalletId())
                .orElseThrow(() ->
                        new RuntimeException("Sender wallet not found"));

        Wallet receiverWallet = walletRepository
                .findById(creditTx.getWalletId())
                .orElseThrow(() ->
                        new RuntimeException("Receiver wallet not found"));

        BigDecimal amount = creditTx.getAmount();

        if (receiverWallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException(
                    "Receiver has insufficient balance for reversal"
            );
        }

        // Reverse balances

        receiverWallet.setBalance(
                receiverWallet.getBalance().subtract(amount)
        );

        senderWallet.setBalance(
                senderWallet.getBalance().add(amount)
        );

        walletRepository.save(receiverWallet);
        walletRepository.save(senderWallet);

        // Mark transactions reversed

        transactions.forEach(tx ->
                tx.setStatus(TransactionStatus.REVERSED));

        transactionRepository.saveAll(transactions);
    }
}

