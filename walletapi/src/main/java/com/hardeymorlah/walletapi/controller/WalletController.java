package com.hardeymorlah.walletapi.controller;

import com.hardeymorlah.walletapi.dto.TransferRequest;
import com.hardeymorlah.walletapi.dto.WalletResponse;
import com.hardeymorlah.walletapi.entity.User;
import com.hardeymorlah.walletapi.entity.Wallet;
import com.hardeymorlah.walletapi.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

    @RestController
    @RequestMapping("/api/v1/wallet")
    @RequiredArgsConstructor
    public class WalletController {

        private final WalletService walletService;

        // 1. Create wallet
        @PostMapping("/create")
        public ResponseEntity<WalletResponse> createWallet() {
            return ResponseEntity.ok(walletService.createWallet());
        }

        // 2. Get wallet
        @GetMapping("/me")
        public ResponseEntity<WalletResponse> getMyWallet() {
            return ResponseEntity.ok(walletService.getWallet());
        }

        // 3. Credit wallet
        @PostMapping("/credit")
        public ResponseEntity<WalletResponse> creditWallet(
                @RequestParam BigDecimal amount) {
            return ResponseEntity.ok(walletService.creditWallet(amount));
        }

        // 4. Debit wallet
        @PostMapping("/debit")
        public ResponseEntity<WalletResponse> debitWallet(
                @RequestParam BigDecimal amount
        ) {
            return ResponseEntity.ok(walletService.debitWallet(amount));
    }
        @PostMapping("/transfer")
        public ResponseEntity<WalletResponse> transfer(@RequestBody TransferRequest request) {
            return ResponseEntity.ok(walletService.transfer(request));
        }
    }

