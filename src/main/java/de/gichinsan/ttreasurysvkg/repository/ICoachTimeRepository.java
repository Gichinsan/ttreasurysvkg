package de.gichinsan.ttreasurysvkg.repository;

import de.gichinsan.ttreasurysvkg.model.Coach;
import de.gichinsan.ttreasurysvkg.model.CoachTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICoachTimeRepository extends JpaRepository<CoachTime, Long> {

    @Query("SELECT c FROM Coach c " +
            "LEFT JOIN FETCH c.coachTimes ct " +
            "WHERE c.id = :id " +
            "ORDER BY ct.trainDate ASC, ct.startTime ASC")
    Optional<Coach> findByIdWithCoachTimes(@Param("id") Long id);
}
