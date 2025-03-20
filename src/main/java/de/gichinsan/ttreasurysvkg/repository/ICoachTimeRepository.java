package de.gichinsan.ttreasurysvkg.repository;

import de.gichinsan.ttreasurysvkg.model.CoachTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICoachTimeRepository extends JpaRepository<CoachTime, Long> {
}
