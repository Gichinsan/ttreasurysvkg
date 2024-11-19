package de.gichinsan.ttreasurysvkg.repository;

import de.gichinsan.ttreasurysvkg.model.ClubImage;
import de.gichinsan.ttreasurysvkg.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITeamRepository extends JpaRepository<Team,Long> {
}
