package de.gichinsan.ttreasurysvkg.service;

import de.gichinsan.ttreasurysvkg.model.Team;
import de.gichinsan.ttreasurysvkg.repository.ITeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService implements ITeamService {

    @Autowired
    private ITeamRepository iTeamRepository;

    @Override
    public List<Team> getAllTeamMembers() {
        return iTeamRepository.findAll();
    }

    @Override
    public void save(Team team) {
        iTeamRepository.save(team);
    }

    @Override
    public Team getTeamMemberById(Long id) {
        return iTeamRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        iTeamRepository.deleteById(id);
    }


}
