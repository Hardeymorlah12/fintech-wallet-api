package com.hardeymorlah.walletapi.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserPageResponse {

    private long totalItems;

    private int totalPages;

    private int currentPage;

    private List<UserResponse> users;
}

