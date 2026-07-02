package de.gichinsan.ttreasurysvkg.service;

import de.gichinsan.ttreasurysvkg.model.Team;
import de.gichinsan.ttreasurysvkg.model.Turnament;
import de.gichinsan.ttreasurysvkg.repository.ITeamRepository;
import de.gichinsan.ttreasurysvkg.repository.ITurnamentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private ITeamRepository iTeamRepository;

    @Mock
    private ITurnamentRepository turnamentRepository;

    @InjectMocks
    private TeamService teamService;

    @Test
    void shouldDetectDuplicateByFirstNameLastNameAndBirthDate() {
        Date birthDate = new Date(0L);

        when(iTeamRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndBirthDate("Max", "Mustermann", birthDate))
                .thenReturn(true);

        assertTrue(teamService.isDuplicate("Max", "Mustermann", birthDate));
        verify(iTeamRepository).existsByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndBirthDate("Max", "Mustermann", birthDate);
    }

    @Test
    void shouldRemoveTeamFromTurnamentsBeforeDeleting() {
        Team team = new Team();
        team.setId(1L);

        Turnament turnament = new Turnament();
        turnament.setPlayers(new ArrayList<>(List.of(team)));

        when(iTeamRepository.findById(1L)).thenReturn(java.util.Optional.of(team));
        when(turnamentRepository.findAll()).thenReturn(List.of(turnament));

        teamService.deleteById(1L);

        assertTrue(turnament.getPlayers().isEmpty());
        verify(turnamentRepository).save(turnament);
        verify(iTeamRepository).deleteById(1L);
    }
}
