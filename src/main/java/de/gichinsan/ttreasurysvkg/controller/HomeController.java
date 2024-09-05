package de.gichinsan.ttreasurysvkg.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {


    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/help")
    public String help() {
        return "help";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login?logout";
    }
}



