package de.gichinsan.ttreasurysvkg.controller;

import de.gichinsan.ttreasurysvkg.model.ClubManagerUser;
import de.gichinsan.ttreasurysvkg.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class KontoController extends BaseController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/konto")
    public String showKonto(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        ClubManagerUser user = getCurrentClubManagerUser(userDetails);
        model.addAttribute("user", user);
        return "konto";

    }

    @PostMapping("/konto")
    public String updateKontoBeschreibung(@AuthenticationPrincipal UserDetails userDetails,
                                          @RequestParam String kontoBeschreibung,
                                          RedirectAttributes redirectAttributes) {
        @Valid ClubManagerUser user = getCurrentClubManagerUser(userDetails);
        user.setKontoBeschreibung(kontoBeschreibung);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Beschreibung erfolgreich aktualisiert");
        return "redirect:/konto";


    }


}
