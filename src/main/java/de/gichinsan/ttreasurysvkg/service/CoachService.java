package de.gichinsan.ttreasurysvkg.service;

import de.gichinsan.ttreasurysvkg.model.Coach;
import de.gichinsan.ttreasurysvkg.model.CoachTime;
import de.gichinsan.ttreasurysvkg.repository.ICoachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CoachService implements ICoachService {

    @Autowired
    private ICoachRepository iCoachRepository;

    @Override
    @Transactional
    public List<Coach> getAllCoaches() {
        return iCoachRepository.findAll();
    }

    @Override
    @Transactional
    public List<Coach> findAllById(List<Long> ids) {
        return iCoachRepository.findAllById(ids);
    }

    @Override
    public void save(Coach coach) {
        iCoachRepository.save(coach);
    }

    // Methode, um Coach mit CoachTimes zu laden
    @Transactional(readOnly = true)
    public Coach getCoachByIdWithCoachTimes(Long id) {
        return iCoachRepository.findByIdWithCoachTimes(id);
    }


    @Override
    public Coach getCoachById(Long id) {
        return iCoachRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        iCoachRepository.deleteById(id);
    }

    @Override
    public void addCoachTime(Long coachId, CoachTime newCoachTime) {
        Coach coach = iCoachRepository.findByIdWithCoachTimes(coachId);
        if (coach != null) {
            coach.addCoachTime(newCoachTime);
            iCoachRepository.save(coach); // Speichern mit neuen Zeiten
        } else {
            throw new RuntimeException("Coach not found");
        }
    }

    @Override
    public void removeCoachTime(Long coachId, Long timeId) {
        Coach coach = iCoachRepository.findByIdWithCoachTimes(coachId);
        if (coach != null) {
            CoachTime timeToRemove = coach.getCoachTimes()
                    .stream()
                    .filter(time -> time.getId().equals(timeId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("CoachTime not found"));
            coach.removeCoachTime(timeToRemove);
            iCoachRepository.save(coach);
        }
    }
}
