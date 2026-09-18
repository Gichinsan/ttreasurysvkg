package de.gichinsan.ttreasurysvkg.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RoundRobinPlanner {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private RoundRobinPlanner() {
    }

    public static Plan create(String teamsText, String startTime, String endTime,
                              Integer matchMinutes, Integer pauseMinutes) {
        List<String> teams = parseTeams(teamsText);
        if (teams.size() < 2) {
            throw new IllegalArgumentException("Mindestens zwei Teams sind erforderlich.");
        }
        if (matchMinutes == null || matchMinutes < 1 || pauseMinutes == null || pauseMinutes < 0) {
            throw new IllegalArgumentException("Spieldauer und Pause müssen gültige Minutenwerte sein.");
        }

        LocalTime start = parseTime(startTime, "Startzeit");
        LocalTime end = parseTime(endTime, "Endzeit");
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Die Startzeit muss vor der Endzeit liegen.");
        }

        List<String> rotatingTeams = new ArrayList<>(teams);
        if (rotatingTeams.size() % 2 != 0) {
            rotatingTeams.add(null);
        }

        List<Match> matches = new ArrayList<>();
        LocalTime roundStart = start;
        int fieldCount = rotatingTeams.size() / 2;
        for (int round = 0; round < rotatingTeams.size() - 1; round++) {
            LocalTime roundEnd = roundStart.plusMinutes(matchMinutes);
            if (roundEnd.isAfter(end)) {
                throw new IllegalArgumentException("Das Zeitfenster reicht nicht für alle Runden.");
            }

            for (int field = 0; field < fieldCount; field++) {
                String teamOne = rotatingTeams.get(field);
                String teamTwo = rotatingTeams.get(rotatingTeams.size() - 1 - field);
                if (teamOne != null && teamTwo != null) {
                    matches.add(new Match(field + 1, round + 1, roundStart, roundEnd, teamOne, teamTwo));
                }
            }

            String lastTeam = rotatingTeams.remove(rotatingTeams.size() - 1);
            rotatingTeams.add(1, lastTeam);
            roundStart = roundStart.plusMinutes(matchMinutes + pauseMinutes);
        }
        return new Plan(matches);
    }

    private static List<String> parseTeams(String teamsText) {
        if (teamsText == null) {
            return Collections.emptyList();
        }
        return teamsText.lines()
                .map(String::trim)
                .filter(team -> !team.isEmpty())
                .toList();
    }

    private static LocalTime parseTime(String value, String label) {
        try {
            return LocalTime.parse(value, TIME_FORMAT);
        } catch (DateTimeParseException | NullPointerException exception) {
            throw new IllegalArgumentException(label + " muss im Format HH:mm angegeben werden.");
        }
    }

    public record Match(int field, int round, LocalTime start, LocalTime end,
                        String teamOne, String teamTwo) {
        public String startText() {
            return TIME_FORMAT.format(start);
        }

        public String endText() {
            return TIME_FORMAT.format(end);
        }
    }

    public record Plan(List<Match> matches) {
        public Plan {
            matches = List.copyOf(matches);
        }

        public String toMarkdown() {
            StringBuilder markdown = new StringBuilder();
            markdown.append("# Turnierplan\n\n");
            markdown.append("| Feld | Runde | Startzeit | Endzeit | Vereinsname Team 1 | Vereinsname Team 2 | Ergebnis |\n");
            markdown.append("|---|---|---|---|---|---|---|\n");
            for (Match match : matches) {
                markdown.append("| ").append(match.field())
                        .append(" | ").append(match.round())
                        .append(" | ").append(match.startText())
                        .append(" | ").append(match.endText())
                        .append(" | ").append(match.teamOne())
                        .append(" | ").append(match.teamTwo())
                        .append(" |  |\n");
            }
            return markdown.toString();
        }
    }
}