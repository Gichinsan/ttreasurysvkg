package de.gichinsan.ttreasurysvkg.controller;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import de.gichinsan.ttreasurysvkg.model.ClubManagerUser;
import de.gichinsan.ttreasurysvkg.model.ClubTransaction;
import de.gichinsan.ttreasurysvkg.model.TransactionType;
import de.gichinsan.ttreasurysvkg.repository.ITransactionRepository;
import de.gichinsan.ttreasurysvkg.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Controller
public class TransactionController extends BaseController{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ITransactionRepository ITransactionRepository;

    @GetMapping("/transactions")
    public String viewTransactions(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        ClubManagerUser user = getCurrentClubManagerUser(userDetails);
        List<ClubTransaction> clubTransactions = ITransactionRepository.findByUser(user);

        double balance = clubTransactions.stream()
                .mapToDouble(transaction ->
                        TransactionType.HABEN.equals(transaction.getType())
                                ? transaction.getAmount()
                                : -transaction.getAmount())
                .sum();

        String totald = String.format(Locale.GERMAN, "%,.2f", balance);

        model.addAttribute("totalCost", totald);
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
        ITransactionRepository.save(clubTransaction);

        return "redirect:/transactions";
    }

    @GetMapping("/transactions/export")
    public ResponseEntity<byte[]> downloadTransactionsAsPdf(@AuthenticationPrincipal UserDetails userDetails) throws IOException {
        String username = userDetails.getUsername();
        ClubManagerUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden: " + username));

        List<ClubTransaction> transactions = ITransactionRepository.findByUser(user);

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
        document.add(new Paragraph("Stand vom " + LocalDate.now()));

        Table table = new Table(new float[]{1, 5, 2, 2, 2});
        table.addHeaderCell("ID");
        table.addHeaderCell("Beschreibung");
        table.addHeaderCell("Type");
        table.addHeaderCell("Betrag");
        table.addHeaderCell("Datum");

        for (ClubTransaction transaction : transactions) {
            table.addCell(String.valueOf(transaction.getId()));
            table.addCell(transaction.getDescription());
            table.addCell(transaction.getType().name());
            table.addCell(String.valueOf(transaction.getAmount()));
            table.addCell(transaction.getDate().toString());
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
