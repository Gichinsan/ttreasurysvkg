package de.gichinsan.ttreasurysvkg.repository;

import de.gichinsan.ttreasurysvkg.model.ClubManagerUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<ClubManagerUser, Long> {
    Optional<ClubManagerUser> findByUsername(String username);

    ClubManagerUser findByEmail(String email);

    ClubManagerUser findByKontoCode(String kontoCode);
}
