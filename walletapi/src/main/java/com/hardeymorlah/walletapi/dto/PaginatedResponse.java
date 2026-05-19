package com.hardeymorlah.walletapi.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

    @Data
    @Builder
    public class PaginatedResponse<T> {

        private List<T> transactions;

        private int currentPage;

        private int totalPages;

        private long totalItems;
    }

