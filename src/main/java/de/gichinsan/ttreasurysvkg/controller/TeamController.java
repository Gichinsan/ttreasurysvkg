package de.gichinsan.ttreasurysvkg.controller;

import de.gichinsan.ttreasurysvkg.model.AgeGroups;
import de.gichinsan.ttreasurysvkg.model.Team;
import de.gichinsan.ttreasurysvkg.service.TeamService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class TeamController {

    @Autowired
    private TeamService teamService;

    @GetMapping("/team")
    public String register(Model model) {
        model.addAttribute("team", teamService.getAllTeamMembers());
        return "team";
    }


    @GetMapping("/team/create")
    public String showCreateForm(Model model) {
        model.addAttribute("team", new Team());
        return "addTeamMember";
    }


    @PostMapping("/team/create")
    public String createTeamMember(@ModelAttribute Team team, HttpSession session) {
        Long id = (Long) session.getAttribute("teammemberId"); // ID aus der Session holen
        if (id != null) {
            team.setId(id);
            Team teammember = teamService.getTeamMemberById(id);
            teammember.setFirstName(team.getFirstName());
            teammember.setLastName(team.getLastName());
            teammember.setBirthDate(team.getBirthDate());
            teammember.setAgeGroup(team.getAgeGroup());
            teamService.save(teammember);
            session.removeAttribute("teammemberId"); // ID aus der Session entfernen
        } else {
            teamService.save(team);
        }
        return "redirect:/team";
    }

    @GetMapping("/team/update/{id}")
    public String editTeamMember(@PathVariable Long id, Model model, HttpSession session) {
        Team teamMember = teamService.getTeamMemberById(id);
        session.setAttribute("teammemberId", id); // ID in Session speichern
        model.addAttribute("team", teamMember);
        return "addTeamMember";
    }

    @PostMapping("/team/remove")
    public String removeTeamMember(@RequestParam Long id) {
        teamService.deleteById(id);
        return "redirect:/team";
    }

    @GetMapping("/team/upload")
    public String uploadTeamList(Model model) {
        model.addAttribute("teamList", new Team());
        return "uploadTeamList";
    }

    @PostMapping("/upload/teamList")
    public String handlecsvFileUpload(@RequestParam("csvfile") MultipartFile file, Model model) {
        try {
            byte[] fileBytes = file.getBytes();
            String decodedContent = decodeText(fileBytes);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(decodedContent.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8))) {

            reader.readLine();
            String line;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Set<String> seenInUpload = new HashSet<>();
            int skippedDuplicates = 0;
            int importedMembers = 0;

            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(";");
                if (columns.length < 9) {
                    continue;
                }

                String firstName = columns[1].trim();
                String lastName = columns[0].trim();
                Date birthDate = dateFormat.parse(columns[2].trim());
                String identityKey = (firstName + "|" + lastName + "|" + birthDate).toLowerCase();

                if (seenInUpload.contains(identityKey) || teamService.isDuplicate(firstName, lastName, birthDate)) {
                    skippedDuplicates++;
                    continue;
                }

                seenInUpload.add(identityKey);

                Team member = new Team();
                member.setFirstName(firstName);
                member.setLastName(lastName);
                member.setBirthDate(birthDate);
                member.setNationalitaet(columns[3].trim());
                member.setAStatus(columns[4].trim());
                member.setVsStatus(columns[5].trim());
                member.setPassnummer(columns[6].trim());
                member.setSpielrechtAb(columns[7].trim());
                member.setRegistriertAm(dateFormat.parse(columns[8].trim()));
                member.setAgeGroup(AgeGroups.pending);

                teamService.save(member);
                importedMembers++;
            }

                String message = "Datei erfolgreich hochgeladen und verarbeitet!";
                if (importedMembers == 0 && skippedDuplicates > 0) {
                    message = "Keine neuen Teammitglieder gespeichert. " + skippedDuplicates + " Datensätze wurden als Duplikate übersprungen.";
                } else if (skippedDuplicates > 0) {
                    message += " " + skippedDuplicates + " Datensätze wurden als Duplikate übersprungen.";
                }
                model.addAttribute("message", message);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Fehler beim Verarbeiten der Datei: " + e.getMessage());
            return "uploadTeamList";
        }
        return "uploadTeamList";
    }

    String decodeText(byte[] input) {
        if (input == null || input.length == 0) {
            return "";
        }

        if (input.length >= 3 && input[0] == (byte) 0xEF && input[1] == (byte) 0xBB && input[2] == (byte) 0xBF) {
            return new String(input, StandardCharsets.UTF_8);
        }
        if (input.length >= 2 && input[0] == (byte) 0xFF && input[1] == (byte) 0xFE) {
            return new String(input, StandardCharsets.UTF_16);
        }
        if (input.length >= 2 && input[0] == (byte) 0xFE && input[1] == (byte) 0xFF) {
            return new String(input, StandardCharsets.UTF_16BE);
        }

        try {
            String utf8Text = new String(input, StandardCharsets.UTF_8);
            if (!utf8Text.contains("\uFFFD")) {
                return utf8Text;
            }
        } catch (Exception ignored) {
            // fall through to fallback charset
        }

        return new String(input, Charset.forName("windows-1252"));
    }

    @GetMapping("/team/statistik")
    public String teamStatistik(Model model) {
        List<Team> teamMembers = teamService.getAllTeamMembers();

        Map<AgeGroups, Long> statusCounts = teamMembers.stream()
                .collect(Collectors.groupingBy(Team::getAgeGroup, Collectors.counting()));

        for (AgeGroups group : AgeGroups.values()) {
            statusCounts.putIfAbsent(group, 0L);
        }

        model.addAttribute("statusCounts", statusCounts);
        model.addAttribute("team", teamService.getAllTeamMembers());
        return "teamStatistik";
    }


}
