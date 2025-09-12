package de.gichinsan.ttreasurysvkg.service;

import de.gichinsan.ttreasurysvkg.model.Coach;
import de.gichinsan.ttreasurysvkg.model.CoachTime;

import java.util.List;

public interface ICoachService {

    List<Coach> getAllCoaches();

    List<Coach> findAllById(List<Long> ids);

    void save(Coach coach);

    Coach getCoachByIdWithCoachTimes(Long id);

    Coach getCoachById(Long id);

    void deleteById(Long id);

    void addCoachTime(Long coachId, CoachTime newCoachTime);

    void removeCoachTime(Long coachId, Long timeId);

    Coach getCoachWithTimes(Long coachId);
}

