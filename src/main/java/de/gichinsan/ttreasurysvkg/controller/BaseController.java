package de.gichinsan.ttreasurysvkg.controller;

import de.gichinsan.ttreasurysvkg.model.ClubManagerUser;
import de.gichinsan.ttreasurysvkg.model.ClubTransaction;
import de.gichinsan.ttreasurysvkg.model.TransactionType;
import de.gichinsan.ttreasurysvkg.repository.ITransactionRepository;
import de.gichinsan.ttreasurysvkg.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Locale;

public abstract class BaseController {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    private ITransactionRepository iTransactionRepository;


    protected ClubManagerUser getCurrentClubManagerUser(UserDetails userDetails) {
        String username = userDetails.getUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden: " + username));
    }

    protected String getResult(ClubManagerUser user) {
        List<ClubTransaction> clubTransactions = iTransactionRepository.findByUser(user);

        double balance = clubTransactions.stream()
                .mapToDouble(transaction ->
                        TransactionType.HABEN.equals(transaction.getType())
                                ? transaction.getAmount()
                                : -transaction.getAmount())
                .sum();

        return String.format(Locale.GERMAN, "%,.2f", balance);
    }
}
