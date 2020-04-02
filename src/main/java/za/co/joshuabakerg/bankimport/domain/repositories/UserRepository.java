package za.co.joshuabakerg.bankimport.domain.repositories;

import org.springframework.stereotype.Repository;

import za.co.joshuabakerg.bankimport.domain.entities.User;

/**
 * @author Joshua Baker on 17/12/2019
 */
public interface UserRepository {

    User save(final User user);

    User findUserByEmail(final String email);

    void deleteAll();

}
