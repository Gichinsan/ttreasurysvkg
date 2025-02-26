package de.gichinsan.ttreasurysvkg.controller;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TextAlignment;
import de.gichinsan.ttreasurysvkg.model.ClubManagerUser;
import de.gichinsan.ttreasurysvkg.model.Coach;
import de.gichinsan.ttreasurysvkg.model.Team;
import de.gichinsan.ttreasurysvkg.model.Turnament;
import de.gichinsan.ttreasurysvkg.service.CoachService;
import de.gichinsan.ttreasurysvkg.service.TeamService;
import de.gichinsan.ttreasurysvkg.service.TurnamentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class TurnamentController extends BaseController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private CoachService coachService;

    @Autowired
    private TurnamentService turnamentService;

    @GetMapping("/turnament")
    public String turnamentMain(Model model) {

        List<Turnament> turnametList = turnamentService.getAllTurnaments();
        model.addAttribute("turnamentList", turnametList);

        return "turnament";
    }

    @GetMapping("/turnament/create")
    public String createTurnament(Model model) {
        model.addAttribute("turnament", new Turnament());
        return "turnamentWizard";
    }

    @PostMapping("/tw2")
    public String goToStep2(@ModelAttribute Turnament turnament, HttpSession session) {
        session.setAttribute("sturnament", turnament);
        return "redirect:/turnamentWizard2";
    }

    @GetMapping("/turnamentWizard2")
    public String turnamentMain(Model model, HttpSession session) {
        Turnament turnament = (Turnament) session.getAttribute("sturnament");
        List<Team> teamMembers = teamService.getTeamMembersByAgeGroup(turnament.getAgeGroup());
        List<Coach> coaches = coachService.getAllCoaches();

        model.addAttribute("turnament", turnament);
        model.addAttribute("teamMembers", teamMembers);
        model.addAttribute("coaches", coaches);

        return "turnamentWizard2";
    }


    @PostMapping("/reviewKader")
    public String selectedKader(HttpSession session, @RequestParam(value = "selectedCoaches", required = false) List<Long> selectedCoachIds, @RequestParam(value = "selectedPlayers", required = false) List<Long> selectedPlayerIds) {

        if (selectedCoachIds != null) {
            session.setAttribute("selectedCoaches", selectedCoachIds);
        }

        if (selectedPlayerIds != null) {
            session.setAttribute("selectedPlayers", selectedPlayerIds);
        }

        return "redirect:/turnamentWizard3";
    }

    @GetMapping("/turnamentWizard3")
    public String reviewKader(Model model, HttpSession session) {
        Turnament turnament = (Turnament) session.getAttribute("sturnament");

        List<Long> selectedCoachesList = (List<Long>) session.getAttribute("selectedCoaches");
        List<Long> selectedPlayersList = (List<Long>) session.getAttribute("selectedPlayers");

        if (selectedCoachesList == null) selectedCoachesList = new ArrayList<>();
        if (selectedPlayersList == null) selectedPlayersList = new ArrayList<>();

        List<Coach> selectedCoachList = coachService.findAllById(selectedCoachesList);
        List<Team> selectedPlayerList = teamService.findAllById(selectedPlayersList);


        String coachesString = selectedCoachList.stream()
                .map(coach -> coach.getFirstName() + " " + coach.getLastName())
                .collect(Collectors.joining(", "));

        String playersString = selectedPlayerList.stream()
                .map(player -> player.getFirstName() + " " + player.getLastName())
                .collect(Collectors.joining(", "));

        model.addAttribute("coachesString", coachesString);
        model.addAttribute("playersString", playersString);
        model.addAttribute("selectedCoaches", selectedCoachesList);
        model.addAttribute("selectedPlayers", selectedPlayersList);
        model.addAttribute("turnament", turnament);

        return "turnamentWizard3";
    }

    @PostMapping("/reviewAndSave")
    public String saveTurnamentKader(HttpSession session, SessionStatus sessionStatus) {

        Object coachObject = session.getAttribute("selectedCoaches");
        Object playerObject = session.getAttribute("selectedPlayers");

        List<Long> selectedCoachesList = new ArrayList<>();
        List<Long> selectedPlayersList = new ArrayList<>();

        Turnament turnament = (Turnament) session.getAttribute("sturnament");

        if (coachObject instanceof List<?>) {
            selectedCoachesList = ((List<?>) coachObject).stream()
                    .filter(Long.class::isInstance)
                    .map(Long.class::cast)
                    .toList();
        }

        if (playerObject instanceof List<?>) {
            selectedPlayersList = ((List<?>) playerObject).stream()
                    .filter(Long.class::isInstance)
                    .map(Long.class::cast)
                    .toList();
        }

        if (turnament != null) {
            turnamentService.saveTurnament(turnament, selectedCoachesList, selectedPlayersList);
        }

        session.removeAttribute("sturnament");
        session.removeAttribute("selectedCoaches");
        session.removeAttribute("selectedPlayers");

        return "turnamentWizard4";
    }

    @GetMapping("/turnamentWizard4")
    public String turnamentWizard4(Model model) {
        return "turnamentWizard4";
    }


    @PostMapping("/backToStart")
    public String notePage(HttpSession session, SessionStatus sessionStatus) {
        return "redirect:/turnament";
    }

    @PostMapping("/turnament/remove")
    public String removeTurnament(@RequestParam Long id) {
        turnamentService.removeTurnament(id);
        return "redirect:/turnament";
    }

    @PostMapping("/backtotw2")
    public String backtoStep2(Model model, HttpSession session) {
        session.removeAttribute("selectedCoaches");
        session.removeAttribute("selectedPlayers");
        return "redirect:/turnamentWizard2";
    }

    @PostMapping("/turnament/edit")
    public String editTurnament(@RequestParam Long id, HttpSession session, Model model) {
        Optional<Turnament> turnamentOpt = turnamentService.getTurnamentById(id);

        if (turnamentOpt.isPresent()) {
            Turnament turnament = turnamentOpt.get();
            session.setAttribute("sturnament", turnament);
            model.addAttribute("turnament", turnament);
        } else {
            return "redirect:/turnament";
        }

        return "turnamentWizard";
    }

    @PostMapping("/turnament/export")
    public ResponseEntity<byte[]> exportTurnarment(@RequestParam Long id, @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        ClubManagerUser user = getCurrentClubManagerUser(userDetails);
        Optional<Turnament> turnamentOpt = turnamentService.getTurnamentById(id);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(36, 36, 36, 36); // Standardränder setzen

        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

        // Tabelle für Kopfbereich (Titel & Logo)
        Table headerTable = new Table(new float[]{3, 1}).useAllAvailableWidth();
        headerTable.setMarginBottom(20);

        // Titel links
        Cell titleCell = new Cell().add(new Paragraph("Turnierplan").setFont(boldFont).setFontSize(16))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.LEFT);
        headerTable.addCell(titleCell);

        // Logo rechts
        if (user.getClubImage() != null) {
            String imagePath = user.getClubImage().getPath();
            ImageData imageData = ImageDataFactory.create(imagePath);
            Image logo = new Image(imageData);
            logo.scaleToFit(100, 50); // Größe anpassen
            Cell logoCell = new Cell().add(logo)
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.RIGHT);
            headerTable.addCell(logoCell);
        } else {
            headerTable.addCell(new Cell().setBorder(Border.NO_BORDER)); // Leere Zelle falls kein Logo
        }
        document.add(headerTable);

        if (turnamentOpt.isPresent()) {
            Turnament turnament = turnamentOpt.get();

            // Tabelle für Turnierinfos
            Table infoTable = new Table(new float[]{3, 5}).useAllAvailableWidth();
            infoTable.setMarginBottom(20);

            infoTable.addCell(new Cell().add(new Paragraph("Verein:").setFont(boldFont)).setBorder(Border.NO_BORDER));
            infoTable.addCell(new Cell().add(new Paragraph(turnament.getClub())).setBorder(Border.NO_BORDER));

            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.GERMAN);
            String date = dateFormat.format(turnament.getTuranmentDate());
            infoTable.addCell(new Cell().add(new Paragraph("Datum:").setFont(boldFont)).setBorder(Border.NO_BORDER));
            infoTable.addCell(new Cell().add(new Paragraph(date)).setBorder(Border.NO_BORDER));

            infoTable.addCell(new Cell().add(new Paragraph("Altersgruppe:").setFont(boldFont)).setBorder(Border.NO_BORDER));
            infoTable.addCell(new Cell().add(new Paragraph(String.valueOf(turnament.getAgeGroup()))).setBorder(Border.NO_BORDER));

            infoTable.addCell(new Cell().add(new Paragraph("Ort:").setFont(boldFont)).setBorder(Border.NO_BORDER));
            infoTable.addCell(new Cell().add(new Paragraph(turnament.getPlace())).setBorder(Border.NO_BORDER));

            document.add(infoTable);

            document.add(new LineSeparator(new SolidLine()));

            // Trainer Liste
            String coachNames = turnament.getCoaches().stream()
                    .map(coach -> coach.getFirstName() + " " + coach.getLastName())
                    .collect(Collectors.joining(", "));

            document.add(new Paragraph("Trainer:").setFont(boldFont).setMarginTop(10));
            document.add(new Paragraph(coachNames));

            // Spieler Liste als Aufzählung
            document.add(new Paragraph("Spieler:").setFont(boldFont).setMarginTop(10));

            com.itextpdf.layout.element.List playerList = new com.itextpdf.layout.element.List().setSymbolIndent(12).setListSymbol("\u2022"); // Bullet-Point-Liste
            turnament.getPlayers().forEach(player ->
                    playerList.add(new ListItem(player.getFirstName() + " " + player.getLastName()))
            );
            document.add(playerList);
        }

        document.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "turnierplan.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(out.toByteArray());
    }

}
