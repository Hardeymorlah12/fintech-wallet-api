package com.hardeymorlah.walletapi.dto;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardResponse {

    private BigDecimal walletBalance;

    private BigDecimal totalCredits;

    private BigDecimal totalDebits;

    private Long totalTransactions;

    private Long successfulTransactions;

    private Long reversedTransactions;
}

