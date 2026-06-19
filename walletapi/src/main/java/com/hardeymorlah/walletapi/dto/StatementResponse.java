package com.hardeymorlah.walletapi.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class StatementResponse {

    private BigDecimal totalCredits;

    private BigDecimal totalDebits;

    private BigDecimal netBalanceChange;

    private List<TransactionResponse> transactions;
}

