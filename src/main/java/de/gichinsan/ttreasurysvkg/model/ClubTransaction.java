package de.gichinsan.ttreasurysvkg.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class ClubTransaction {

    @Id
    @Column(name = "trans_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trans_seq")
    @SequenceGenerator(name = "trans_seq", sequenceName = "trans_seq", allocationSize = 1)
    private Long id;

    @NotNull
    private String type;

    @NotNull
    private Double amount;

    @NotNull
    private LocalDate date;

    private String description;

    @ManyToOne
    @JoinColumn(name = "auser_id")
    private ClubManagerUser user;
}
