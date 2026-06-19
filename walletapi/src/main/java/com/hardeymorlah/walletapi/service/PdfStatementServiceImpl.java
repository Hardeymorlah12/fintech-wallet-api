package com.hardeymorlah.walletapi.service;


import com.hardeymorlah.walletapi.dto.StatementResponse;
import com.hardeymorlah.walletapi.dto.TransactionResponse;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;


import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

@Service
public class PdfStatementServiceImpl
        implements PdfStatementService {

    @Override
    public byte[] generateStatementPdf(
            StatementResponse statement
    ) {

        try {

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            Document document = new Document();

            PdfWriter.getInstance(
                    document,
                    outputStream
            );

            document.open();

            document.add(
                    new Paragraph(
                            "Wallet Statement Report"
                    )
            );

            document.add(
                    new Paragraph("Wallet Statement")
            );

            document.add(
                    new Paragraph(
                            "Generated At: "
                                    + LocalDateTime.now()
                    )
            );

            document.add(
                    new Paragraph(" ")
            );

            document.add(
                    new Paragraph(
                            "Total Credits: NGN "
                                    + statement.getTotalCredits()
                    )
            );

            document.add(
                    new Paragraph(
                            "Total Debits: NGN "
                                    + statement.getTotalDebits()
                    )
            );

            document.add(
                    new Paragraph(
                            "Net Balance Change: NGN "
                                    + statement.getNetBalanceChange()
                    )
            );

            document.add(
                    new Paragraph(" ")
            );

            PdfPTable table =
                    new PdfPTable(5);

            table.addCell("Reference");
            table.addCell("Type");
            table.addCell("Status");
            table.addCell("Amount");
            table.addCell("Date");

            for (TransactionResponse tx :
                    statement.getTransactions()) {

                table.addCell(tx.getReference());
                table.addCell(tx.getType().name());
                table.addCell(tx.getStatus().name());
                table.addCell(
                        tx.getAmount().toString()
                );
                table.addCell(
                        tx.getCreatedAt().toString()
                );
            }

            document.add(table);

            document.close();

            return outputStream.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to generate PDF",
                    e
            );
        }
    }
}

