package com.hardeymorlah.walletapi.service;


import com.hardeymorlah.walletapi.dto.StatementResponse;

public interface PdfStatementService {

        byte[] generateStatementPdf(
                StatementResponse statement
        );
    }

