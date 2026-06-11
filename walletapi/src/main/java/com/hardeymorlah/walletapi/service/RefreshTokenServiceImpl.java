package com.hardeymorlah.walletapi.service;

import com.hardeymorlah.walletapi.entity.RefreshToken;
import com.hardeymorlah.walletapi.entity.User;
import com.hardeymorlah.walletapi.repository.RefreshTokenRepository;
import com.hardeymorlah.walletapi.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Transactional
    @Override
    public RefreshToken createRefreshToken(User user) {

        Optional<RefreshToken> existingToken =
                refreshTokenRepository.findByUser(user);

        if (existingToken.isPresent()) {

            RefreshToken token = existingToken.get();

            token.setToken(UUID.randomUUID().toString());
            token.setExpiryDate(LocalDateTime.now().plusDays(7));

            return refreshTokenRepository.save(token);
        }

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }
    @Override
    public RefreshToken verifyRefreshToken(String token) {

        RefreshToken refreshToken =
                refreshTokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException("Refresh token not found"));

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        return refreshToken;
    }
    @Override
    public String generateNewAccessToken(String refreshToken) {

        RefreshToken token = verifyRefreshToken(refreshToken);

        User user = token.getUser();

        return jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );
    }
}