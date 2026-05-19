package com.hardeymorlah.walletapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
@NotNull
@Positive
public class TransferRequest {

    private Long toUserId;
    private BigDecimal amount;
}