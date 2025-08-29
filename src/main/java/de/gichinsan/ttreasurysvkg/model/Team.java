package de.gichinsan.ttreasurysvkg.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Data
public class Team {

    @Id
    @Column(name = "team_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "team_seq")
    @SequenceGenerator(name = "team_seq", sequenceName = "team_seq", allocationSize = 1)
    private Long id;

    @NotEmpty(message = "Vorname darf nicht leer sein")
    private String firstName;

    @NotEmpty(message = "Nachname darf nicht leer sein")
    private String lastName;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    private String nationalitaet;

    private String aStatus;

    private String vsStatus;

    private String passnummer;

    private String spielrechtAb;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date registriertAm;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AgeGroups ageGroup;


}
