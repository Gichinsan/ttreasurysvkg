package de.gichinsan.ttreasurysvkg.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class Turnament {

    @Id
    @Column(name = "turnament_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "turnament_seq")
    @SequenceGenerator(name = "turnament_seq", sequenceName = "turnament_seq", allocationSize = 1)
    private Long id;

    private String club;

    private String place;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date turanmentDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AgeGroups ageGroup;

    @ManyToMany (fetch = FetchType.EAGER)
    @JoinTable(
            name = "turnament_coach",
            joinColumns = @JoinColumn(name = "turnament_id"),
            inverseJoinColumns = @JoinColumn(name = "coach_id")
    )
    private List<Coach> coaches = new ArrayList<>();

    @ManyToMany (fetch = FetchType.EAGER)
    @JoinTable(
            name = "turnament_player",
            joinColumns = @JoinColumn(name = "turnament_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private List<Team> players = new ArrayList<>();
}
