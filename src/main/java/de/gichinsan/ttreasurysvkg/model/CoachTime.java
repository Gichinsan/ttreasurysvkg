package de.gichinsan.ttreasurysvkg.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;
import java.util.Date;

@Entity
@Data
public class CoachTime {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "coach_time_seq")
    @SequenceGenerator(name = "coach_time_seq", sequenceName = "coach_time_seq", allocationSize = 1)
    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date trainDate;

    private String trainHours;

    @NotNull(message = "Startzeit darf nicht leer sein")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull(message = "Endzeit darf nicht leer sein")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "coach_id", nullable = false)
    private Coach coach;

    @PrePersist
    @PreUpdate
    private void calculateTrainHours() {
        if (startTime != null && endTime != null) {
            long hours = java.time.Duration.between(startTime, endTime).toHours();
            long minutes = java.time.Duration.between(startTime, endTime).toMinutes() % 60;
            this.trainHours = hours + " Stunden " + minutes + " Minuten";
        }
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    private TrainType trainType;

}
