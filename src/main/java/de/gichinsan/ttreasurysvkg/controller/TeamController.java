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
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
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
    public String createTeamMember(@ModelAttribute Team team, HttpSession session) throws IOException {
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
    public String removeTeamMember(@RequestParam Long id) throws IOException {
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
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length < 9) {
                    continue;
                }

                Team member = new Team();
                member.setFirstName(columns[1].trim());
                member.setLastName(columns[0].trim());
                member.setBirthDate(new SimpleDateFormat("dd.MM.yyyy").parse(columns[2].trim()));
                member.setNationalitaet(columns[3].trim());
                member.setAStatus(columns[4].trim());
                member.setVsStatus(columns[5].trim());
                member.setPassnummer(columns[6].trim());
                member.setSpielrechtAb(columns[7].trim());
                member.setRegistriertAm(new SimpleDateFormat("dd.MM.yyyy").parse(columns[8].trim()));
                member.setAgeGroup(AgeGroups.pending);

                teamService.save(member);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Fehler beim Verarbeiten der Datei: " + e.getMessage());
            return "uploadTeamList";
        }

        model.addAttribute("message", "Datei erfolgreich hochgeladen und verarbeitet!");
        return "uploadTeamList";
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
