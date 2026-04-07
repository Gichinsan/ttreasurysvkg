package de.gichinsan.ttreasurysvkg.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Coach {

    @Id
    @Column(name = "coach_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Vorname darf nicht leer sein")
    private String firstName;

    @NotEmpty(message = "Nachname darf nicht leer sein")
    private String lastName;

    private String trainHours;

    @OneToMany(mappedBy = "coach", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("trainDate ASC, startTime ASC")
    @ToString.Exclude
    private List<CoachTime> coachTimes = new ArrayList<>();

    @Override
    public String toString() {
        return "Coach{id=" + id + ", firstName='" + firstName + "', lastName='" + lastName + "'}";
    }

    public String getTotalTrainHours() {
        long totalMinutes = coachTimes.stream()
                .filter(ct -> ct.getStartTime() != null && ct.getEndTime() != null)
                .mapToLong(ct -> java.time.Duration.between(ct.getStartTime(), ct.getEndTime()).toMinutes())
                .sum();

        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        return hours + " Stunden " + minutes + " Minuten";
    }

    public void addCoachTime(CoachTime coachTime) {
        coachTimes.add(coachTime);
        coachTime.setCoach(this);
    }

    public void removeCoachTime(CoachTime coachTime) {
        coachTimes.remove(coachTime);
        coachTime.setCoach(null);
    }
}
