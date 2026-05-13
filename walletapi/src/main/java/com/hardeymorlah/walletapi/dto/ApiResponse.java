package com.hardeymorlah.walletapi.dto;

import java.time.LocalDateTime;

public class ApiResponse<T> {

        private boolean success;
        private String message;
        private T data;
        private LocalDateTime timestamp;
    }

