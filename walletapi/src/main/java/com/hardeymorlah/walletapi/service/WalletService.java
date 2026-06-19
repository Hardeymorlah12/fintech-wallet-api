package com.hardeymorlah.walletapi.service;

import com.hardeymorlah.walletapi.dto.DashboardResponse;
import com.hardeymorlah.walletapi.dto.StatementResponse;
import com.hardeymorlah.walletapi.dto.TransferRequest;
import com.hardeymorlah.walletapi.dto.WalletResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface WalletService {

    WalletResponse transfer(TransferRequest request);

    WalletResponse createWallet();

    WalletResponse getWallet();

    WalletResponse creditWallet(BigDecimal amount);

    WalletResponse debitWallet(BigDecimal amount);

    StatementResponse generateStatement(
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    DashboardResponse getDashboard();

}