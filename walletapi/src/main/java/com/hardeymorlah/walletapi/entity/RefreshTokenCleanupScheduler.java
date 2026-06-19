package com.hardeymorlah.walletapi.entity;

import com.hardeymorlah.walletapi.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
    @RequiredArgsConstructor
    @Slf4j
    public class RefreshTokenCleanupScheduler {

        private final RefreshTokenRepository refreshTokenRepository;

        @Scheduled(cron = "0 0 0 * * *")
        public void deleteExpiredTokens() {

            refreshTokenRepository
                    .deleteByExpiryDateBefore(
                            LocalDateTime.now()
                    );

            log.info(
                    "Expired refresh tokens cleaned up"
            );
        }
    }

