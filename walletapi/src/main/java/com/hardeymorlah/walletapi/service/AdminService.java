package com.hardeymorlah.walletapi.service;

import com.hardeymorlah.walletapi.dto.AuditLogPageResponse;
import com.hardeymorlah.walletapi.dto.UserPageResponse;
import com.hardeymorlah.walletapi.dto.WalletPageResponse;
import com.hardeymorlah.walletapi.entity.TransactionPageResponse;
import org.springframework.transaction.annotation.Transactional;

public interface AdminService {


    void freezeWallet(Long walletId);

    void unfreezeWallet(Long walletId);

    void promoteUser(Long userId);

    void demoteUser(Long userId);

    UserPageResponse getAllUsers(int page, int size);

    WalletPageResponse getAllWallets(int page, int size);

    TransactionPageResponse getAllTransactions(
            int page,
            int size
    );
    AuditLogPageResponse getAuditLogs(
            int page,
            int size
    );
//
//    @Transactional
//    void reverseTransaction(String reference);

    void reverseTransfer(String groupReference);
}



