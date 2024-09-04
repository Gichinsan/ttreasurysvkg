package de.gichinsan.ttreasurysvkg.service;

import de.gichinsan.ttreasurysvkg.model.ClubImage;

import java.util.List;

public interface IClubImageService {

    List<ClubImage> getAllClubImages();

    void save(ClubImage clubImage);

    ClubImage getClubImageById(Long id);

}
