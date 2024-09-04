package de.gichinsan.ttreasurysvkg.service;

import de.gichinsan.ttreasurysvkg.model.ClubManagerUser;
import de.gichinsan.ttreasurysvkg.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

import static org.springframework.security.core.userdetails.User.withUsername;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ClubManagerUser user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden: " + username));

        if (user == null) {
            throw new UsernameNotFoundException("Benutzer nicht gefunden");
        }
        User.UserBuilder builder = withUsername(username);
        builder.password(user.getPassword());
        builder.authorities(Collections.singletonList(new SimpleGrantedAuthority(user.getRole())));
        return builder.build();
    }
}
