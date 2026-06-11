package com.hardeymorlah.walletapi.entity;

import com.hardeymorlah.walletapi.dto.TransactionResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
    public class TransactionPageResponse {

        private long totalItems;

        private int totalPages;

        private int currentPage;

        private List<TransactionResponse> transactions;
    }


