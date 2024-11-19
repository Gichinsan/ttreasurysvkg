package de.gichinsan.ttreasurysvkg.repository;

import de.gichinsan.ttreasurysvkg.model.TrikotSatz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITrikotSatzRepository extends JpaRepository<TrikotSatz, Long> {
}

