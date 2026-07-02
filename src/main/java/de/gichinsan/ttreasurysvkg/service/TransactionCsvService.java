package de.gichinsan.ttreasurysvkg.service;

import de.gichinsan.ttreasurysvkg.model.ClubManagerUser;
import de.gichinsan.ttreasurysvkg.model.ClubTransaction;
import de.gichinsan.ttreasurysvkg.model.TransactionType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class TransactionCsvService {

    private static final DateTimeFormatter EXPORT_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter IMPORT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public String exportTransactions(List<ClubTransaction> transactions) {
        StringBuilder csv = new StringBuilder();
        csv.append("date;type;amount;description\n");
        for (ClubTransaction transaction : transactions) {
            csv.append(transaction.getDate().format(EXPORT_DATE_FORMATTER)).append(';');
            csv.append(transaction.getType()).append(';');
            csv.append(String.format(java.util.Locale.GERMANY, "%.2f", transaction.getAmount())).append(';');
            csv.append(escape(transaction.getDescription())).append('\n');
        }
        return csv.toString();
    }

    public List<ClubTransaction> importTransactions(String csvContent, ClubManagerUser user) {
        List<ClubTransaction> transactions = new ArrayList<>();
        List<String> lines = Arrays.stream(csvContent.split("\\R")).filter(line -> !line.isBlank()).toList();

        if (lines.isEmpty()) {
            return transactions;
        }

        for (int i = 1; i < lines.size(); i++) {
            String[] columns = lines.get(i).split(";", 4);
            if (columns.length < 4) {
                continue;
            }

            ClubTransaction transaction = new ClubTransaction();
            transaction.setDate(parseDate(columns[0].trim()));
            transaction.setType(TransactionType.valueOf(columns[1].trim().toUpperCase()));
            transaction.setAmount(Double.parseDouble(columns[2].trim().replace(',', '.')));
            transaction.setDescription(unescape(columns[3].trim()));
            transaction.setUser(user);
            transactions.add(transaction);
        }
        return transactions;
    }

    private LocalDate parseDate(String rawValue) {
        try {
            return LocalDate.parse(rawValue, IMPORT_DATE_FORMATTER);
        } catch (DateTimeParseException ignored) {
            return LocalDate.parse(rawValue, ISO_DATE_FORMATTER);
        }
    }

    public boolean isDuplicate(ClubTransaction transaction, List<ClubTransaction> existingTransactions) {
        return existingTransactions.stream().anyMatch(existing ->
                Objects.equals(existing.getDate(), transaction.getDate())
                        && existing.getType() == transaction.getType()
                        && Objects.equals(existing.getAmount(), transaction.getAmount())
                        && Objects.equals(existing.getDescription(), transaction.getDescription())
        );
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\"", "\"\"");
    }

    private String unescape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\"\"", "\"");
    }
}
