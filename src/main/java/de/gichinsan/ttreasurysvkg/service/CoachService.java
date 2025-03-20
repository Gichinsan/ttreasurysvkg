package de.gichinsan.ttreasurysvkg.service;

import de.gichinsan.ttreasurysvkg.model.Coach;
import de.gichinsan.ttreasurysvkg.model.CoachTime;
import de.gichinsan.ttreasurysvkg.repository.ICoachRepository;
import de.gichinsan.ttreasurysvkg.repository.ICoachTimeRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CoachService implements ICoachService {


    @Autowired
    private ICoachRepository iCoachRepository;

    @Autowired
    private ICoachTimeRepository coachTimeRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public List<Coach> getAllCoaches() {
        return iCoachRepository.findAllWithCoachTimes();
    }

    @Override
    @Transactional
    public List<Coach> findAllById(List<Long> ids) {
        return iCoachRepository.findAllById(ids);
    }

    @Override
    @Transactional
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
    @Transactional
    public void addCoachTime(Long coachId, CoachTime newCoachTime) {
        Coach coach = iCoachRepository.findByIdWithCoachTimes(coachId);
        if (coach != null) {
            if (newCoachTime.getId() != null) {
                Optional<CoachTime> existingCoachTime = coachTimeRepository.findById(newCoachTime.getId());
                if (existingCoachTime.isPresent()) {
                    newCoachTime = existingCoachTime.get();
                } else {
                    System.out.println("Warnung: CoachTime mit ID " + newCoachTime.getId() + " existiert nicht! Erstelle eine neue.");
                    newCoachTime.setId(null);
                }
            }

            newCoachTime.setCoach(coach);
            coach.addCoachTime(newCoachTime);

            coachTimeRepository.save(newCoachTime);
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

    @Override
    @Transactional
    public Optional<Coach> getCoachWithTimes(Long coachId) {
        return Optional.ofNullable(iCoachRepository.findByIdWithCoachTimes(coachId));
    }
}
