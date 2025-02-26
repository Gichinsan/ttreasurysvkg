package de.gichinsan.ttreasurysvkg.service;

import de.gichinsan.ttreasurysvkg.model.Coach;
import de.gichinsan.ttreasurysvkg.model.Team;
import de.gichinsan.ttreasurysvkg.model.Turnament;
import de.gichinsan.ttreasurysvkg.repository.ICoachRepository;
import de.gichinsan.ttreasurysvkg.repository.ITeamRepository;
import de.gichinsan.ttreasurysvkg.repository.ITurnamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TurnamentService implements ITurnamentService {

    @Autowired
    private ITurnamentRepository turnamentRepository;

    @Autowired
    private ICoachRepository coachRepository;

    @Autowired
    private ITeamRepository teamRepository;


    @Override
    public List<Turnament> getAllTurnaments() {
        return turnamentRepository.findAll();
    }

    @Override
    public void saveTurnament(Turnament turnament, List<Long> coachIds, List<Long> playerIds) {
        List<Coach> selectedCoaches = coachRepository.findAllById(coachIds);
        List<Team> selectedPlayers = teamRepository.findAllById(playerIds);

        turnament.setCoaches(selectedCoaches);
        turnament.setPlayers(selectedPlayers);

        turnamentRepository.save(turnament);
    }

    @Override
    public void removeTurnament(Long id) {
        turnamentRepository.deleteById(id);
    }

    @Override
    public Optional<Turnament> getTurnamentById(Long id) {
        return turnamentRepository.findById(id);
    }
}
