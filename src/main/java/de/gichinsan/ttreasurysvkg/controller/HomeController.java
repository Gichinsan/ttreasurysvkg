package de.gichinsan.ttreasurysvkg.controller;

import de.gichinsan.ttreasurysvkg.model.ClubImage;
import de.gichinsan.ttreasurysvkg.model.ClubManagerUser;
import de.gichinsan.ttreasurysvkg.model.ClubTransaction;
import de.gichinsan.ttreasurysvkg.repository.ITransactionRepository;
import de.gichinsan.ttreasurysvkg.repository.UserRepository;
import de.gichinsan.ttreasurysvkg.service.ClubImageService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
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
    private ITransactionRepository ITransactionRepository;

    @Autowired
    private ClubImageService clubImageService;


    public static final String ROLE_ADMIN = "Admin";
    public static final String ROLE_KASSENWART = "Kassenwart";


    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new ClubManagerUser());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") ClubManagerUser user,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

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
     *
     * @return
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

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/konto")
    public String showKonto(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        ClubManagerUser user = getCurrentClubManagerUser(userDetails);
        model.addAttribute("user", user);
        return "konto";

    }

    @PostMapping("/konto")
    public String updateKontoBeschreibung(@AuthenticationPrincipal UserDetails userDetails,
                                          @RequestParam String kontoBeschreibung,
                                          @Valid @ModelAttribute("user") ClubManagerUser user,
                                          BindingResult bindingResult,
                                          RedirectAttributes redirectAttributes) {
        user = getCurrentClubManagerUser(userDetails);

        user.setKontoBeschreibung(kontoBeschreibung);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Beschreibung erfolgreich aktualisiert");
        return "redirect:/konto";


    }


    @GetMapping("/transactions")
    public String viewTransactions(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        ClubManagerUser user = getCurrentClubManagerUser(userDetails);

        List<ClubTransaction> clubTransactions = ITransactionRepository.findByUser(user);
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
            bindingResult.reject("error.user", "Das hat nicht geklappt!");
            return "addTransaction";
        }
        ClubManagerUser user = getCurrentClubManagerUser(userDetails);

        clubTransaction.setUser(user);
        clubTransaction.setDate(LocalDate.now());
        ITransactionRepository.save(clubTransaction);

        return "redirect:/transactions";
    }

    private ClubManagerUser getCurrentClubManagerUser(UserDetails userDetails) {
        String username = userDetails.getUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden: " + username));
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login?logout";
    }

    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        return "konto";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@AuthenticationPrincipal UserDetails userDetails,
                                   @RequestParam("file") MultipartFile file,
                                   Model model) throws IOException {

        String fileName = file.getOriginalFilename();
        String uploadDir = new File("uploaded-images").getAbsolutePath() + "/";

        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            boolean dirCreated = uploadDirectory.mkdirs();
            if (!dirCreated) {
                model.addAttribute("message", "Failed to create upload directory!");
                return "konto";
            }
        }

        File destinationFile = new File(uploadDir + fileName);

        try {
            file.transferTo(destinationFile);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "File upload failed: " + e.getMessage());
            return "konto";
        }

        ClubImage clubImage = new ClubImage();
        clubImage.setPath(uploadDir + fileName);
        clubImage.setImageName(fileName);
        clubImageService.save(clubImage);

        ClubManagerUser user = getCurrentClubManagerUser(userDetails);

        user.setClubImage(clubImage);
        userRepository.save(user);

        model.addAttribute("user", user);
        model.addAttribute("message", "Image uploaded successfully!");
        return "konto";
    }


}



