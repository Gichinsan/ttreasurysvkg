package de.gichinsan.ttreasurysvkg.service;

import de.gichinsan.ttreasurysvkg.model.ClubImage;
import de.gichinsan.ttreasurysvkg.repository.IClubImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClubImageService implements IClubImageService {

    @Autowired
    private IClubImageRepository iClubImageRepository;

    @Override
    public List<ClubImage> getAllClubImages() {
        return iClubImageRepository.findAll();
    }

    @Override
    public void save(ClubImage clubImage) {
        iClubImageRepository.save(clubImage);
    }

    @Override
    public ClubImage getClubImageById(Long id) {
        return iClubImageRepository.findById(id).orElse(null);
    }
}
