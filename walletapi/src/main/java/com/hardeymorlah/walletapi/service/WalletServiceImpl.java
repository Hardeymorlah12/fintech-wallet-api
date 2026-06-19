package com.hardeymorlah.walletapi.service;

import com.hardeymorlah.walletapi.dto.*;
import com.hardeymorlah.walletapi.entity.*;
import com.hardeymorlah.walletapi.exception.*;
import com.hardeymorlah.walletapi.repository.TransactionRepository;
import com.hardeymorlah.walletapi.repository.UserRepository;
import com.hardeymorlah.walletapi.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.hardeymorlah.walletapi.service.AdminServiceImpl.getWalletResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    // =========================
    // JWT USER RESOLUTION
    // =========================
    private User getAuthenticatedUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // =========================
    // TRANSFER (JWT SECURED)
    // =========================
    @Transactional
    @Override
    public WalletResponse transfer(TransferRequest request) {

        User sender = getAuthenticatedUser();
        BigDecimal amount = request.getAmount();

        log.info(
                "Transfer initiated by user ID: {} to user ID: {} amount: {}",
                sender.getId(),
                request.getToUserId(),
                amount
        );

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferException(
                    "Amount must be greater than zero"
            );
        }

        if (sender.getId().equals(request.getToUserId())) {
            throw new InvalidTransferException(
                    "Cannot transfer to same account"
            );
        }

        Wallet senderWallet = walletRepository.findByUserId(sender.getId())
                .orElseThrow(() ->
                        new WalletNotFoundException("Sender wallet not found"));

        if (senderWallet.isFrozen()) {
            throw new WalletFrozenException(
                    "Your wallet is frozen. Transfer not allowed."
            );
        }

        Wallet receiverWallet = walletRepository.findByUserId(
                        request.getToUserId()
                )
                .orElseThrow(() ->
                        new ReceiverWalletNotFoundException(
                                "Receiver wallet not found"
                        ));

        if (receiverWallet.isFrozen()) {
            throw new WalletFrozenException(
                    "Receiver wallet is frozen."
            );
        }

        if (senderWallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Debit sender
        senderWallet.setBalance(
                senderWallet.getBalance().subtract(amount)
        );
        senderWallet.setUpdatedAt(LocalDateTime.now());

        // Credit receiver
        receiverWallet.setBalance(
                receiverWallet.getBalance().add(amount)
        );
        receiverWallet.setUpdatedAt(LocalDateTime.now());

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        String groupReference =
                "TRF-" + System.currentTimeMillis();

// Sender transaction
        recordTransaction(
                senderWallet.getId(),
                amount,
                TransactionType.DEBIT,
                TransactionStatus.SUCCESS,
                groupReference
        );

// Receiver transaction
        recordTransaction(
                receiverWallet.getId(),
                amount,
                TransactionType.CREDIT,
                TransactionStatus.SUCCESS,
                groupReference
        );

        // Sender notification
        notificationService.createNotification(
                sender.getId(),
                "Transfer Successful",
                "You transferred NGN " + amount +
                        " to " +
                        receiverWallet.getUser().getFullName()
        );

        // Receiver notification
        notificationService.createNotification(
                receiverWallet.getUser().getId(),
                "Wallet Credited",
                "You received NGN " + amount +
                        " from " +
                        sender.getFullName()
        );

        log.info(
                "Transfer completed from user {} to user {} amount {}",
                sender.getId(),
                request.getToUserId(),
                amount
        );

        return mapToWalletResponse(senderWallet);
    }

    // =========================
    // TRANSACTION LOGGER
    // =========================
    private void recordTransaction(
            Long walletId,
            BigDecimal amount,
            TransactionType type,
            TransactionStatus status,
            String groupReference
    ) {

        Transaction tx = Transaction.builder()
                .walletId(walletId)
                .amount(amount)
                .type(type)
                .status(status)
                .groupReference(groupReference)
                .reference("TXN-" + System.currentTimeMillis())
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(tx);
    }


    // =========================
    // MAPPER
    // =========================
    private WalletResponse mapToWalletResponse(Wallet wallet) {

        return getWalletResponse(wallet);
    }

    // =========================
    // OTHER METHODS (unchanged)
    // =========================
    @Override
    public WalletResponse createWallet() {

        User user = getAuthenticatedUser();

        boolean walletExists = walletRepository.findByUserId(user.getId()).isPresent();

        if (walletExists) {
            throw new WalletAlreadyExistsException("User already has a wallet");
        }

        Wallet wallet = Wallet.builder()
                .user(user)
                .balance(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Wallet savedWallet = walletRepository.save(wallet);

        return mapToWalletResponse(savedWallet);
    }

    @Override
    public WalletResponse getWallet() {

        User user = getAuthenticatedUser();

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ReceiverWalletNotFoundException("Receiver wallet not found"));

        return mapToWalletResponse(wallet);
    }

    @Override
    public WalletResponse creditWallet(BigDecimal amount) {
        User user = getAuthenticatedUser();

        log.info(
                "Wallet credit initiated by user ID: {} amount: {}",
                user.getId(),
                amount
        );

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.isFrozen()) {
            throw new WalletFrozenException(
                    "Wallet is frozen. Operation not allowed."
            );
        }

        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdatedAt(LocalDateTime.now());

        Wallet updatedWallet = walletRepository.save(wallet);

        recordTransaction(
                wallet.getId(),
                amount,
                TransactionType.CREDIT,
                TransactionStatus.SUCCESS,
                null
        );

        // Create notification
        notificationService.createNotification(
                user.getId(),
                "Wallet Credited",
                "Your wallet was credited with NGN " + amount

        );
        log.info(
                "Wallet credited successfully for user ID: {}",
                user.getId()
        );

        // Send email
        emailService.sendEmail(
                user.getEmail(),
                "Wallet Credited",
                "Your wallet has been credited with NGN " + amount
        );

        return mapToWalletResponse(updatedWallet);
    }

    @Override
    public WalletResponse debitWallet(BigDecimal amount) {

        User user = getAuthenticatedUser();

        log.info(
                "Wallet debit initiated by user ID: {} amount: {}",
                user.getId(),
                amount
        );

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));

        if (wallet.isFrozen()) {
            throw new WalletFrozenException(
                    "Wallet is frozen. Operation not allowed."
            );
        }

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setUpdatedAt(LocalDateTime.now());

        Wallet updatedWallet = walletRepository.save(wallet);

        recordTransaction(
                wallet.getId(),
                amount,
                TransactionType.CREDIT,
                TransactionStatus.SUCCESS,
                null
        );
        // Create notification
        notificationService.createNotification(
                user.getId(),
                "Wallet Debited",
                "Your wallet was debited with NGN " + amount
        );

        // Send email
        emailService.sendEmail(
                user.getEmail(),
                "Wallet Debited",
                "Your wallet has been debited with NGN " + amount
        );

        return mapToWalletResponse(updatedWallet);
    }

    @Override
    public StatementResponse generateStatement(
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {

        User user = getAuthenticatedUser();

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found"));

        List<Transaction> transactions =
                transactionRepository.findByWalletIdAndCreatedAtBetween(
                        wallet.getId(),
                        startDate,
                        endDate
                );

        BigDecimal totalCredits = transactions.stream()
                .filter(tx ->
                        tx.getType() == TransactionType.CREDIT)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDebits = transactions.stream()
                .filter(tx ->
                        tx.getType() == TransactionType.DEBIT)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netBalanceChange =
                totalCredits.subtract(totalDebits);

        List<TransactionResponse> transactionResponses =
                transactions.stream()
                        .map(tx -> TransactionResponse.builder()
                                .id(tx.getId())
                                .walletId(tx.getWalletId())
                                .reference(tx.getReference())
                                .groupReference(tx.getGroupReference())
                                .type(tx.getType())
                                .status(tx.getStatus())
                                .amount(tx.getAmount())
                                .createdAt(tx.getCreatedAt())
                                .build())
                        .toList();

        return StatementResponse.builder()
                .totalCredits(totalCredits)
                .totalDebits(totalDebits)
                .netBalanceChange(netBalanceChange)
                .transactions(transactionResponses)
                .build();
    }

    @Override
    public DashboardResponse getDashboard() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Wallet not found"));

        List<Transaction> transactions =
                transactionRepository.findByWalletId(wallet.getId());

        BigDecimal totalCredits = transactions.stream()
                .filter(tx ->
                        tx.getType() == TransactionType.CREDIT
                                && tx.getStatus() == TransactionStatus.SUCCESS)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDebits = transactions.stream()
                .filter(tx ->
                        tx.getType() == TransactionType.DEBIT
                                && tx.getStatus() == TransactionStatus.SUCCESS)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long successfulTransactions = transactions.stream()
                .filter(tx ->
                        tx.getStatus() == TransactionStatus.SUCCESS)
                .count();

        long reversedTransactions = transactions.stream()
                .filter(tx ->
                        tx.getStatus() == TransactionStatus.REVERSED)
                .count();

        return DashboardResponse.builder()
                .walletBalance(wallet.getBalance())
                .totalCredits(totalCredits)
                .totalDebits(totalDebits)
                .totalTransactions((long) transactions.size())
                .successfulTransactions(successfulTransactions)
                .reversedTransactions(reversedTransactions)
                .build();
    }
}

