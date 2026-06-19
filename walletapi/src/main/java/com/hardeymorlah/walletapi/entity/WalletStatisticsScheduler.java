package com.hardeymorlah.walletapi.entity;


import com.hardeymorlah.walletapi.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
    @RequiredArgsConstructor
    @Slf4j
    public class WalletStatisticsScheduler {

        private final WalletRepository walletRepository;

        @Scheduled(cron = "0 0 1 * * *")
        public void generateStatistics() {

            long totalWallets =
                    walletRepository.count();

            log.info(
                    "Daily Wallet Stats -> Total Wallets: {}",
                    totalWallets
            );
        }
    }
