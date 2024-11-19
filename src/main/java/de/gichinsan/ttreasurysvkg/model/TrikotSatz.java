package de.gichinsan.ttreasurysvkg.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class TrikotSatz {

    @Id
    @Column(name = "trikotsatz_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trikotsatz_seq")
    @SequenceGenerator(name = "trikotsatz_seq", sequenceName = "trikotsatz_seq", allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TrikotTyp trikotTyp;

    private int trikotAnzahl;
    private int hoseAnzahl;
    private int stutzenAnzahl;

    private String hoseGroesse;
}
