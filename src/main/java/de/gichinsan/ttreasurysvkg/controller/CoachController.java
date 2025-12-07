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
import de.gichinsan.ttreasurysvkg.model.Coach;
import de.gichinsan.ttreasurysvkg.model.CoachTime;
import de.gichinsan.ttreasurysvkg.model.TrainType;
import de.gichinsan.ttreasurysvkg.service.CoachService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Controller
public class CoachController extends BaseController {

    @Autowired
    private CoachService coachService;

    @GetMapping("/coaches")
    String showCocahes(Model model) {
        model.addAttribute("coaches", coachService.getAllCoaches());
        return "coaches";
    }

    @GetMapping("/coach/create")
    String createCoaches(Model model) {
        model.addAttribute("coach", new Coach());
        return "createcoach";
    }

    @PostMapping("/coach/create")
    public String createCoach(@ModelAttribute Coach coach, HttpSession session) throws IOException {
        coachService.save(coach);
        return "redirect:/coaches";
    }

    @GetMapping("/coach/edit/{id}")
    public String editCoach(@PathVariable Long id, Model model, HttpSession session) {
        Coach coach = coachService.getCoachWithTimes(id);

        session.setAttribute("coach", id);
        model.addAttribute("coach", coach);
        model.addAttribute("trainTypes", TrainType.values());
        model.addAttribute("coachTimes", coach.getCoachTimes());
        model.addAttribute("newCoachTime", new CoachTime());
        return "editcoach";
    }

    @PostMapping("/coach/{id}/addTime")
    public String addCoachTime(@PathVariable Long id, @ModelAttribute CoachTime newCoachTime, Model model) {
        coachService.addCoachTime(id, newCoachTime);
        return "redirect:/coach/edit/" + id; // Zurück zum Bearbeitungsformular
    }

    @PostMapping("/coach/remove")
    public String removeCoach(@RequestParam Long id) throws IOException {
        coachService.deleteById(id);
        return "redirect:/coaches";
    }

    @PostMapping("/coach/{coachId}/removeTime/{timeId}")
    public String removeCoachTime(@PathVariable Long coachId, @PathVariable Long timeId) {
        coachService.removeCoachTime(coachId, timeId);
        return "redirect:/coach/edit/" + coachId;
    }

    @GetMapping("/coach/export/{id}")
    public ResponseEntity<byte[]> exportCoachTimes(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id, Model model, HttpSession session) throws MalformedURLException {
        ClubManagerUser user = getCurrentClubManagerUser(userDetails);
        Coach coach = coachService.getCoachByIdWithCoachTimes(id);
        List<CoachTime> coachTimesList = coach.getCoachTimes();

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

        document.add(new Paragraph("Trainer Name: " + coach.getFirstName() + " " + coach.getLastName()));
        document.add(new Paragraph("Geleistete Stunden: " + coach.getTotalTrainHours()));

        Table table = new Table(new float[]{3, 2, 2, 5});
        table.addHeaderCell("Datum");
        table.addHeaderCell("Startzeit");
        table.addHeaderCell("Endezeit");
        table.addHeaderCell("Art der Einheit");

        DateTimeFormatter germanFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");


        for (CoachTime coachTime : coachTimesList) {
            String formattedDate = coachTime.getTrainDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .format(germanFormatter);
            table.addCell(formattedDate).setTextAlignment(TextAlignment.LEFT);
            table.addCell(String.valueOf(coachTime.getStartTime())).setTextAlignment(TextAlignment.LEFT);
            table.addCell(String.valueOf(coachTime.getEndTime())).setTextAlignment(TextAlignment.LEFT);
            table.addCell(String.valueOf(coachTime.getTrainType())).setTextAlignment(TextAlignment.LEFT);
        }

        document.add(table);
        document.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "trainingszeiten.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(out.toByteArray());
    }

    @GetMapping("/coach/exportxls/{id}")
    public ResponseEntity<byte[]> exportCoachTimesToExcel(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id, Model model, HttpSession session) throws IOException {
        ClubManagerUser user = getCurrentClubManagerUser(userDetails);
        Coach coach = coachService.getCoachByIdWithCoachTimes(id);
        List<CoachTime> coachTimesList = coach.getCoachTimes();

        // Erstelle eine neue Excel-Datei
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Trainerzeiten");

        // Header-Zeile erstellen
        XSSFRow header = sheet.createRow(1);
        header.createCell(0).setCellValue("Datum");
        header.createCell(1).setCellValue("Art der Einheit");
        header.createCell(2).setCellValue("Anzahl");
        header.createCell(3).setCellValue("Startzeit");
        header.createCell(4).setCellValue("Endezeit");

        // Inhalt-Zellen erstellen
        int y = 2;
        for (CoachTime coachTime : coachTimesList) {
            XSSFRow row = sheet.createRow(y);

            // Datum, Startzeit, Endezeit und Art der Einheit in die Zellen hinzufügen
            row.createCell(0).setCellValue(coachTime.getTrainDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

            DateTimeFormatter LocalTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

            @NotNull(message = "Startzeit darf nicht leer sein") LocalTime start = coachTime.getStartTime();
            @NotNull(message = "Endzeit darf nicht leer sein") LocalTime end = coachTime.getEndTime();

            Duration duration = Duration.between(start, end);
            int hours = duration.toHoursPart();
            int minutes = duration.toMinutesPart();

            double hoursAndHalf = (hours + (double)minutes / 60);

            row.createCell(1).setCellValue(coachTime.getTrainType().name());
            row.createCell(2).setCellValue(hoursAndHalf);
            row.createCell(3).setCellValue(LocalTimeFormat.format(coachTime.getStartTime()));
            row.createCell(4).setCellValue(LocalTimeFormat.format(coachTime.getEndTime()));

            y++;
        }

        // Datei in ein ByteArray umwandeln
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        byte[] bytes = out.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "trainingszeiten.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(bytes);
    }


}
