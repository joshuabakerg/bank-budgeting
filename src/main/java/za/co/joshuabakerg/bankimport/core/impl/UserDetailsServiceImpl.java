package za.co.joshuabakerg.bankimport.core.impl;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import za.co.joshuabakerg.bankimport.core.UserService;

/**
 * @author Joshua Baker on 06/12/2019
 */
@Component
@AllArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(final String s) throws UsernameNotFoundException {
        final long start = System.currentTimeMillis();
        final za.co.joshuabakerg.bankimport.domain.entities.User user = userService.getByEmail(s);
        log.info("Took {}ms to fetch user", System.currentTimeMillis() - start);
        if (user != null) {
            return User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .roles(user.getRoles().split(","))
                    .build();
        }
        return null;
    }

}
