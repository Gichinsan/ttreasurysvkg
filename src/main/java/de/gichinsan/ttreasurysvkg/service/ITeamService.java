package de.gichinsan.ttreasurysvkg.service;

import de.gichinsan.ttreasurysvkg.model.AgeGroups;
import de.gichinsan.ttreasurysvkg.model.ClubImage;
import de.gichinsan.ttreasurysvkg.model.Team;

import java.util.Date;
import java.util.List;

public interface ITeamService {

    List<Team> getAllTeamMembers();

    List<Team> findAllById(List<Long> ids);

    void save(Team team);

    Team getTeamMemberById(Long id);

    void deleteById(Long id);

    List<Team> getTeamMembersByAgeGroup(AgeGroups ageGroup);

    boolean isDuplicate(String firstName, String lastName, Date birthDate);
}
