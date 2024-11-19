package de.gichinsan.ttreasurysvkg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TrainHoursController {


    @GetMapping("/trainhours")
    String getTrainHours(Model model){
        return "trainhours";
    }
}
