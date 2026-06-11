package com.hardeymorlah.walletapi.service;

public interface EmailService {

    void sendEmail(
            String to,
            String subject,
            String body
    );
}

