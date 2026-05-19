package com.hardeymorlah.walletapi.service;

import com.hardeymorlah.walletapi.dto.TransferRequest;
import com.hardeymorlah.walletapi.dto.WalletResponse;

import java.math.BigDecimal;

public interface WalletService {

    WalletResponse transfer(TransferRequest request);

    WalletResponse createWallet();

    WalletResponse getWallet();

    WalletResponse creditWallet(BigDecimal amount);

    WalletResponse debitWallet(BigDecimal amount);
}