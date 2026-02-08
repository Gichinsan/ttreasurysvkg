package de.gichinsan.ttreasurysvkg.controller;

import de.gichinsan.ttreasurysvkg.model.ClubManagerUser;
import de.gichinsan.ttreasurysvkg.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Random;

@Controller
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static final String ROLE_ADMIN = "Admin";
    public static final String ROLE_KASSENWART = "Kassenwart";


    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new ClubManagerUser());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") ClubManagerUser user,
                               BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            bindingResult.reject("error.user", "Das hat nicht geklappt!");
            return "register";
        }

        if (!user.getRole().equals(ROLE_ADMIN) && !user.getRole().equals(ROLE_KASSENWART)) {
            return "redirect:/register?error=InvalidRole";
        }

        if (userRepository.findByEmail(user.getEmail()) != null) {
            bindingResult.rejectValue("email", "error.user", "Diese E-Mail-Adresse wird bereits verwendet");
            return "redirect:/register";
        }

        user.setFirstName(user.getFirstName());
        user.setLastName(user.getLastName());
        user.setUsername(user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole().equals(ROLE_KASSENWART)) {
            user.setKontoCode(generateUniqueKontoCode());
        }
        user.setRole(user.getRole());
        user.setEmail(user.getEmail());

        userRepository.save(user);
        return "redirect:/login";
    }

    /**
     * generateUniqueKontoCode
     *
     * @return String Kontonumber
     */
    private String generateUniqueKontoCode() {
        Random random = new Random();
        String kontoCode;
        do {
            int code = 100000 + random.nextInt(900000);
            kontoCode = String.valueOf(code);
        } while (userRepository.findByKontoCode(kontoCode) != null);
        return kontoCode;
    }
}
