package com.hardeymorlah.walletapi.service;

import com.hardeymorlah.walletapi.entity.RefreshToken;
import com.hardeymorlah.walletapi.entity.User;

public interface RefreshTokenService {

        RefreshToken createRefreshToken(User user);

        RefreshToken verifyRefreshToken(String token);

        String generateNewAccessToken(String refreshToken);
    }

