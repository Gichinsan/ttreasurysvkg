package de.gichinsan.ttreasurysvkg.utils;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoundRobinPlannerTest {

    @Test
    void createsAllMatchesAndAdvancesRounds() {
        String teams = String.join("\n", List.of("Team 1", "Team 2", "Team 3", "Team 4",
                "Team 5", "Team 6", "Team 7", "Team 8"));

        RoundRobinPlanner.Plan plan = RoundRobinPlanner.create(teams, "10:00", "12:00", 10, 2);

        assertEquals(28, plan.matches().size());
        assertEquals("10:00", plan.matches().get(0).startText());
        assertEquals("10:10", plan.matches().get(0).endText());
        assertEquals("10:12", plan.matches().get(4).startText());
        assertEquals(7, plan.matches().stream().map(RoundRobinPlanner.Match::round).distinct().count());
        Set<String> uniqueMatches = plan.matches().stream()
            .map(match -> List.of(match.teamOne(), match.teamTwo()).stream().sorted().collect(Collectors.joining(" - ")))
            .collect(Collectors.toSet());
        assertEquals(28, uniqueMatches.size());
    }

    @Test
    void rejectsAWindowThatCannotFitAllRounds() {
        assertThrows(IllegalArgumentException.class,
                () -> RoundRobinPlanner.create("Team 1\nTeam 2", "10:00", "10:05", 10, 2));
    }
}