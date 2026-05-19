package com.hardeymorlah.walletapi.service;

import com.hardeymorlah.walletapi.dto.TransferRequest;
import com.hardeymorlah.walletapi.dto.UserResponse;
import com.hardeymorlah.walletapi.dto.WalletResponse;
import com.hardeymorlah.walletapi.entity.*;
import com.hardeymorlah.walletapi.exception.InvalidTransferException;
import com.hardeymorlah.walletapi.exception.ReceiverWalletNotFoundException;
import com.hardeymorlah.walletapi.exception.WalletAlreadyExistsException;
import com.hardeymorlah.walletapi.repository.TransactionRepository;
import com.hardeymorlah.walletapi.repository.UserRepository;
import com.hardeymorlah.walletapi.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

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

        if (sender.getId().equals(request.getToUserId())) {
            throw new InvalidTransferException("Cannot transfer to same account");
        }

        Wallet senderWallet = walletRepository.findByUserId(sender.getId())
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

        Wallet receiverWallet = walletRepository.findByUserId(request.getToUserId())
                .orElseThrow(() -> new ReceiverWalletNotFoundException("Receiver wallet not found"));

        BigDecimal amount = request.getAmount();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferException("Amount must be greater than zero");
        }

        if (senderWallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // debit sender
        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        senderWallet.setUpdatedAt(LocalDateTime.now());

        // credit receiver
        receiverWallet.setBalance(receiverWallet.getBalance().add(amount));
        receiverWallet.setUpdatedAt(LocalDateTime.now());

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        // transactions
        recordTransaction(senderWallet.getId(), amount, TransactionType.DEBIT);
        recordTransaction(receiverWallet.getId(), amount, TransactionType.CREDIT);

        return mapToWalletResponse(senderWallet);
    }

    // =========================
    // TRANSACTION LOGGER
    // =========================
    private void recordTransaction(Long walletId, BigDecimal amount, TransactionType type) {

        Transaction tx = Transaction.builder()
                .walletId(walletId)
                .amount(amount)
                .type(type)
                .reference("TXN-" + System.currentTimeMillis())
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(tx);
    }

    // =========================
    // MAPPER
    // =========================
    private WalletResponse mapToWalletResponse(Wallet wallet) {

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
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .build();
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

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdatedAt(LocalDateTime.now());

        Wallet updatedWallet = walletRepository.save(wallet);

        recordTransaction(wallet.getId(), amount, TransactionType.CREDIT);

        return mapToWalletResponse(updatedWallet);
    }


    @Override
    public WalletResponse debitWallet(BigDecimal amount) {

        User user = getAuthenticatedUser();

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setUpdatedAt(LocalDateTime.now());

        Wallet updatedWallet = walletRepository.save(wallet);

        recordTransaction(wallet.getId(), amount, TransactionType.DEBIT);

        return mapToWalletResponse(updatedWallet);
    }
}