package de.gichinsan.ttreasurysvkg.controller;

import de.gichinsan.ttreasurysvkg.model.Team;
import de.gichinsan.ttreasurysvkg.service.TeamService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
            Team teamMember = teamService.getTeamMemberById(team.getId());
            teamService.save(team);
            session.removeAttribute("teammemberId"); // ID aus der Session entfernen
        }

        teamService.save(team);
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

}
