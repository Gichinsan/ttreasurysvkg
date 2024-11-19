package de.gichinsan.ttreasurysvkg.service;

import de.gichinsan.ttreasurysvkg.model.ClubImage;
import de.gichinsan.ttreasurysvkg.model.Team;

import java.util.List;

public interface ITeamService {

    List<Team> getAllTeamMembers();

    void save(Team team);

    Team getTeamMemberById(Long id);

    void deleteById(Long id);
}
