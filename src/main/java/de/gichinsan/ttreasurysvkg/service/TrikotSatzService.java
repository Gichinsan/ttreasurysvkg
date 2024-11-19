package de.gichinsan.ttreasurysvkg.service;

import de.gichinsan.ttreasurysvkg.model.TrikotSatz;
import de.gichinsan.ttreasurysvkg.repository.ITrikotSatzRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrikotSatzService {

    @Autowired
    private ITrikotSatzRepository repository;

    public TrikotSatz save(TrikotSatz trikotSatz) {
        return repository.save(trikotSatz);
    }

    public List<TrikotSatz> findAll() {
        return repository.findAll();
    }

    public TrikotSatz findById(Long id) {
        return repository.findById(id).orElse(null); // Optional Handling
    }
}
