package de.gichinsan.ttreasurysvkg.controller;

import de.gichinsan.ttreasurysvkg.model.ClubImage;
import de.gichinsan.ttreasurysvkg.model.ClubManagerUser;
import de.gichinsan.ttreasurysvkg.repository.UserRepository;
import de.gichinsan.ttreasurysvkg.service.ClubImageService;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
public class UploadController extends BaseController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClubImageService clubImageService;

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

        // Speichere und verkleinere das Bild
        String filePath = uploadDir + fileName;
        resizeAndSaveImage(file, filePath);


        ClubImage clubImage = new ClubImage();
        clubImage.setPath(filePath);
        clubImage.setImageName(fileName);
        clubImageService.save(clubImage);

        ClubManagerUser user = getCurrentClubManagerUser(userDetails);

        user.setClubImage(clubImage);
        userRepository.save(user);

        model.addAttribute("user", user);
        model.addAttribute("message", "Image uploaded successfully!");
        return "konto";
    }

    private void resizeAndSaveImage(MultipartFile file, String outputPath) throws IOException {
        Thumbnails.of(file.getInputStream())
                .size(150, 100)
                .toFile(new File(outputPath));
    }

}
