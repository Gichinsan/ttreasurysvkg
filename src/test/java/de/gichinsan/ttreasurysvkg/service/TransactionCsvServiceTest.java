package de.gichinsan.ttreasurysvkg.service;

import de.gichinsan.ttreasurysvkg.model.ClubManagerUser;
import de.gichinsan.ttreasurysvkg.model.ClubTransaction;
import de.gichinsan.ttreasurysvkg.model.TransactionType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionCsvServiceTest {

    private final TransactionCsvService transactionCsvService = new TransactionCsvService();

    @Test
    void shouldExportTransactionsAsCsv() {
        ClubTransaction transaction = new ClubTransaction();
        transaction.setDate(LocalDate.of(2024, 1, 2));
        transaction.setType(TransactionType.HABEN);
        transaction.setAmount(125.5);
        transaction.setDescription("Mitgliedsbeitrag");

        String csv = transactionCsvService.exportTransactions(List.of(transaction));

        assertTrue(csv.contains("date;type;amount;description"));
        assertTrue(csv.contains("2024-01-02"));
        assertTrue(csv.contains("HABEN"));
        assertTrue(csv.contains("125,50") || csv.contains("125.50"));
        assertTrue(csv.contains("Mitgliedsbeitrag"));
    }

    @Test
    void shouldImportTransactionsFromCsv() {
        String csv = "date;type;amount;description\n02.01.2024;SOLL;50;Training\n";

        List<ClubTransaction> imported = transactionCsvService.importTransactions(csv, new ClubManagerUser());

        assertEquals(1, imported.size());
        assertEquals(LocalDate.of(2024, 1, 2), imported.get(0).getDate());
        assertEquals(TransactionType.SOLL, imported.get(0).getType());
        assertEquals(50.0, imported.get(0).getAmount());
        assertEquals("Training", imported.get(0).getDescription());
    }
}
