package de.gichinsan.ttreasurysvkg.repository;

import de.gichinsan.ttreasurysvkg.model.AgeGroups;
import de.gichinsan.ttreasurysvkg.model.ClubImage;
import de.gichinsan.ttreasurysvkg.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ITeamRepository extends JpaRepository<Team,Long> {
    List<Team> findByAgeGroup(AgeGroups ageGroup);

    List<Team> findAllById(Iterable<Long> ids);
}
