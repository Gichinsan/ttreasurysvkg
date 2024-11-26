package de.gichinsan.ttreasurysvkg.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Entity
@Data
public class Coach {

    @Id
    @Column(name = "coach_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "coach_seq")
    @SequenceGenerator(name = "coach_seq", sequenceName = "coach_seq", allocationSize = 1)
    private Long id;

    @NotEmpty(message = "Vorname darf nicht leer sein")
    private String firstName;

    @NotEmpty(message = "Nachname darf nicht leer sein")
    private String lastName;
}
