package de.gichinsan.ttreasurysvkg.controller;

import de.gichinsan.ttreasurysvkg.model.ClubManagerUser;
import de.gichinsan.ttreasurysvkg.model.ClubTransaction;
import de.gichinsan.ttreasurysvkg.repository.TransactionRepository;
import de.gichinsan.ttreasurysvkg.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") ClubManagerUser user,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (!user.getRole().equals("Admin") && !user.getRole().equals("Kassenwart")) {
            return "redirect:/register?error=InvalidRole";
        }

        if (userRepository.findByEmail(user.getEmail()) != null) {
            redirectAttributes.addFlashAttribute("error", "Diese E-Mail-Adresse wird bereits verwendet");
            return "redirect:/register";
        }

        user.setUsername(user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(user.getRole());
        user.setEmail(user.getEmail());

        if (user.getRole().equals("Kassenwart")) {
            String kontoCode = generateUniqueKontoCode();
            user.setKontoCode(kontoCode);
        }

        userRepository.save(user);
        return "redirect:/login";
    }

    private String generateUniqueKontoCode() {
        Random random = new Random();
        String kontoCode;
        do {
            int code = 100000 + random.nextInt(900000);
            kontoCode = String.valueOf(code);
        } while (userRepository.findByKontoCode(kontoCode) != null);
        return kontoCode;
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/konto")
    public String showKonto(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();
        ClubManagerUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden: " + username));


        model.addAttribute("user", user);
        return "konto";

    }

    @PostMapping("/konto")
    public String updateKontoBeschreibung(@AuthenticationPrincipal UserDetails userDetails,
                                          @RequestParam String kontoBeschreibung,
                                          @Valid @ModelAttribute("user") ClubManagerUser user,
                                          BindingResult bindingResult,
                                          RedirectAttributes redirectAttributes) {
        String username = userDetails.getUsername();
        user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden: " + username));

        user.setKontoBeschreibung(kontoBeschreibung);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Beschreibung erfolgreich aktualisiert");
        return "redirect:/konto";


    }


    @GetMapping("/transactions")
    public String viewTransactions(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();
        ClubManagerUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden: " + username));

        List<ClubTransaction> clubTransactions = transactionRepository.findByUser(user);
        model.addAttribute("transactions", clubTransactions);
        return "transactions";
    }

    @GetMapping("/transactions/add")
    public String addTransactionForm(Model model) {
        ClubTransaction transaction = new ClubTransaction();
        transaction.setDate(LocalDate.now());
        model.addAttribute("transaction", transaction);
        return "addTransaction";
    }

    @PostMapping("/transactions/add")
    public String addTransaction(@AuthenticationPrincipal UserDetails userDetails,
                                 @Valid @ModelAttribute("transaction") ClubTransaction clubTransaction,
                                 BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            System.out.println("Validation errors: " + bindingResult.getAllErrors());
            return "addTransaction";
        }
        String username = userDetails.getUsername();
        ClubManagerUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden: " + username));

        clubTransaction.setUser(user);
        clubTransaction.setDate(LocalDate.now());
        transactionRepository.save(clubTransaction);

        return "redirect:/transactions";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login?logout";
    }

}



