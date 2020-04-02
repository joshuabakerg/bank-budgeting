package za.co.joshuabakerg.bankimport.utils;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Joshua Baker on 02/04/2020
 */
public class SecurityUserBuilder {

    public static UserDetails build(final za.co.joshuabakerg.bankimport.domain.entities.User user) {
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
