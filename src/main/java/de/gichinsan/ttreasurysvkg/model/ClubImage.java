package de.gichinsan.ttreasurysvkg.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ClubImage {

    @Id
    @Column(name = "clubimage_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clubimage_seq")
    @SequenceGenerator(name = "clubimage_seq", sequenceName = "clubimage_seq", allocationSize = 1)
    private Long id;

    private String path;
    private String imageName;

}
