package de.gichinsan.ttreasurysvkg.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
public class ClubManagerUser {

    @Id
    @Column(name = "auser_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auser_seq")
    @SequenceGenerator(name = "auser_seq", sequenceName = "auser_seq", allocationSize = 1)
    private Long id;

    @Column(unique = true)
    @NotEmpty(message = "Benutzername darf nicht leer sein")
    private String username;

    @NotEmpty(message = "Passwort darf nicht leer sein")
    @Size(min = 6, message = "Das Passwort muss mindestens 6 Zeichen lang sein")
    private String password;
    private String role;

    @Email(message = "Ungültige E-Mail-Adresse")
    @NotEmpty(message = "E-Mail darf nicht leer sein")
    private String email;

    @Column(unique = true)
    private String kontoCode;

    private String kontoBeschreibung;

}
