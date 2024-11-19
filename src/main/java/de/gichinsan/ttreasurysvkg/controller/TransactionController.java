package de.gichinsan.ttreasurysvkg.controller;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import de.gichinsan.ttreasurysvkg.model.ClubManagerUser;
import de.gichinsan.ttreasurysvkg.model.ClubTransaction;
import de.gichinsan.ttreasurysvkg.repository.ITransactionRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Controller
public class TransactionController extends BaseController {

    @Autowired
    private ITransactionRepository iTransactionRepository;

    @GetMapping("/transactions")
    public String viewTransactions(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        ClubManagerUser user = getCurrentClubManagerUser(userDetails);
        List<ClubTransaction> clubTransactions = iTransactionRepository.findByUser(user);
        String result = getResult(user);

        model.addAttribute("totalCost", result);
        model.addAttribute("transactions", clubTransactions);
        return "transactions";
    }


    @GetMapping("/transactions/add")
    public String addTransactionForm(Model model) {
        ClubTransaction transaction = new ClubTransaction();
        transaction.setDate(LocalDate.now());
        model.addAttribute("transaction", transaction);
        return "addTransaction";
    }

    @PostMapping("/transactions/add")
    public String addTransaction(@AuthenticationPrincipal UserDetails userDetails,
                                 @Valid @ModelAttribute("transaction") ClubTransaction clubTransaction,
                                 BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            bindingResult.reject("error.user", "Das hat nicht geklappt!");
            return "addTransaction";
        }
        ClubManagerUser user = getCurrentClubManagerUser(userDetails);

        clubTransaction.setUser(user);
        clubTransaction.setDate(LocalDate.now());
        iTransactionRepository.save(clubTransaction);

        return "redirect:/transactions";
    }

    @GetMapping("/transactions/export")
    public ResponseEntity<byte[]> downloadTransactionsAsPdf(@AuthenticationPrincipal UserDetails userDetails) throws IOException {
        ClubManagerUser user = getCurrentClubManagerUser(userDetails);
        List<ClubTransaction> transactions = iTransactionRepository.findByUser(user);

        String result = getResult(user);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Logo oben rechts hinzufügen
        if (user.getClubImage() != null) {
            String imagePath = user.getClubImage().getPath();
            ImageData imageData = ImageDataFactory.create(imagePath);
            Image logo = new Image(imageData);
            logo.setFixedPosition(pdfDoc.getDefaultPageSize().getWidth() - 150, pdfDoc.getDefaultPageSize().getHeight() - 100);
            document.add(logo);
        }

        document.add(new Paragraph("Kontonummer " + user.getKontoCode()));
        document.add(new Paragraph("Transaktionen von " + user.getKontoBeschreibung()));
        try {
            result = result.trim().replace(",", ".");

            if (!result.matches("^-?\\d+(\\.\\d+)?$")) {
                document.add(new Paragraph("Fehler: Ungültiger Wert für den Kontostand."));
            }

            double resultValue = Double.parseDouble(result);
            NumberFormat currencyFormatter = DecimalFormat.getCurrencyInstance(Locale.GERMANY);
            String formattedBalance = currencyFormatter.format(resultValue);

            document.add(new Paragraph("Aktueller Kontostand: " + formattedBalance));
        } catch (NumberFormatException e) {
            document.add(new Paragraph("Fehler: Ungültiger Wert für den Kontostand."));
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu", Locale.GERMAN);
        LocalDate now = LocalDate.now();
        document.add(new Paragraph("Stand vom " + now.format(formatter)));

        Table table = new Table(new float[]{1, 5, 2, 3, 2});
        table.addHeaderCell("ID");
        table.addHeaderCell("Beschreibung");
        table.addHeaderCell("Type");
        table.addHeaderCell("Betrag");
        table.addHeaderCell("Datum");

        for (ClubTransaction transaction : transactions) {
            table.addCell(String.valueOf(transaction.getId())).setTextAlignment(TextAlignment.LEFT);
            table.addCell(transaction.getDescription()).setTextAlignment(TextAlignment.LEFT);
            table.addCell(transaction.getType().name()).setTextAlignment(TextAlignment.LEFT);
            table.addCell(DecimalFormat.getCurrencyInstance(Locale.GERMANY).format(transaction.getAmount())).setTextAlignment(TextAlignment.RIGHT);
            table.addCell(transaction.getDate().format(formatter));
        }

        document.add(table);
        document.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "transactions.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(out.toByteArray());
    }

}
