package de.gichinsan.ttreasurysvkg.service;

import de.gichinsan.ttreasurysvkg.model.Turnament;

import java.util.List;
import java.util.Optional;

public interface ITurnamentService {

    List<Turnament> getAllTurnaments();

    void saveTurnament(Turnament turnament, List<Long> coachIds, List<Long> playerIds);

    void removeTurnament(Long id);

    Optional<Turnament> getTurnamentById(Long id);

    Turnament savePlan(Turnament turnament);
}
