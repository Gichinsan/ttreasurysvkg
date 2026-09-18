package de.gichinsan.ttreasurysvkg.service;

import de.gichinsan.ttreasurysvkg.model.AgeGroups;
import de.gichinsan.ttreasurysvkg.model.Team;
import de.gichinsan.ttreasurysvkg.model.Turnament;
import de.gichinsan.ttreasurysvkg.repository.ITeamRepository;
import de.gichinsan.ttreasurysvkg.repository.ITurnamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TeamService implements ITeamService {

    @Autowired
    private ITeamRepository iTeamRepository;

    @Autowired
    private ITurnamentRepository turnamentRepository;

    @Override
    public List<Team> getAllTeamMembers() {
        return iTeamRepository.findAll();
    }

    @Override
    public List<Team> findAllById(List<Long> ids) {
        return iTeamRepository.findAllById(ids);
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
        Team team = iTeamRepository.findById(id).orElse(null);
        if (team == null) {
            return;
        }

        List<Turnament> turnaments = turnamentRepository.findAll();
        for (Turnament turnament : turnaments) {
            if (turnament.getPlayers() != null && turnament.getPlayers().remove(team)) {
                turnamentRepository.save(turnament);
            }
        }

        iTeamRepository.deleteById(id);
    }

    @Override
    public List<Team> getTeamMembersByAgeGroup(AgeGroups ageGroup) {
        if (ageGroup == AgeGroups.F) {
            List<Team> fTeams = new java.util.ArrayList<>(iTeamRepository.findByAgeGroup(AgeGroups.F1));
            fTeams.addAll(iTeamRepository.findByAgeGroup(AgeGroups.F2));
            return fTeams;
        }
        return iTeamRepository.findByAgeGroup(ageGroup);
    }

    @Override
    public boolean isDuplicate(String firstName, String lastName, Date birthDate) {
        return iTeamRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndBirthDate(firstName, lastName, birthDate);
    }

}
