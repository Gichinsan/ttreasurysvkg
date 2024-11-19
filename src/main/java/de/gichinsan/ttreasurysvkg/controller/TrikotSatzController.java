package de.gichinsan.ttreasurysvkg.controller;

import de.gichinsan.ttreasurysvkg.model.TrikotSatz;
import de.gichinsan.ttreasurysvkg.service.TrikotSatzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

@Controller
public class TrikotSatzController {

    @Autowired
    private TrikotSatzService trikotSatzService;

    // Seite für das Formular anzeigen
    @GetMapping("/trikots/erstellen")
    public String showCreateForm(Model model) {
        model.addAttribute("trikotSatz", new TrikotSatz());
        return "addKit";
    }

    @GetMapping("/trikots/update/{id}")
    public String editTrikotSatz(@PathVariable Long id, Model model) {
        // Der spezifische Trikotsatz wird anhand der ID geladen
        TrikotSatz trikotSatz = trikotSatzService.findById(id);

        if (trikotSatz == null) {
            // Handle den Fall, dass der Trikotsatz nicht gefunden wurde (optional)
            return "redirect:/trikots/liste"; // Leite zurück, falls nicht vorhanden
        }

        model.addAttribute("trikotSatz", trikotSatz);
        return "addKit"; // Das Formular für das Bearbeiten anzeigen
    }

    @PostMapping("/trikots/update/{id}")
    public String updateTrikotSatz(@PathVariable Long id,
                                   @ModelAttribute TrikotSatz trikotSatz) throws IOException {

        TrikotSatz existingTrikotSatz = trikotSatzService.findById(id);

        if (existingTrikotSatz == null) {
            // Falls der Trikotsatz nicht existiert
            return "redirect:/trikots/liste";
        }

        // Aktualisiere die vorhandenen Felder
        existingTrikotSatz.setTrikotAnzahl(trikotSatz.getTrikotAnzahl());
        existingTrikotSatz.setHoseAnzahl(trikotSatz.getHoseAnzahl());
        existingTrikotSatz.setStutzenAnzahl(trikotSatz.getStutzenAnzahl());
        existingTrikotSatz.setHoseGroesse(trikotSatz.getHoseGroesse());


        trikotSatzService.save(existingTrikotSatz);
        return "redirect:/kit";
    }


    @PostMapping("/trikots/erstellen")
    public String createTrikotSatz(@ModelAttribute TrikotSatz trikotSatz) throws IOException {

        trikotSatzService.save(trikotSatz);
        return "redirect:/kit";
    }


    @GetMapping("/kit")
    public String listTrikotSaetze(Model model) {
        model.addAttribute("trikots", trikotSatzService.findAll());
        return "kit";
    }


}
