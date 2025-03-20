package de.gichinsan.ttreasurysvkg.repository;

import de.gichinsan.ttreasurysvkg.model.Coach;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICoachRepository extends JpaRepository<Coach, Long> {
    @Query("SELECT c FROM Coach c LEFT JOIN FETCH c.coachTimes WHERE c.id = :id")
    Coach findByIdWithCoachTimes(@Param("id") Long id);

    @Query("SELECT c FROM Coach c LEFT JOIN FETCH c.coachTimes")
    List<Coach> findAllWithCoachTimes();

    List<Coach> findAllById(Iterable<Long> id);


}
