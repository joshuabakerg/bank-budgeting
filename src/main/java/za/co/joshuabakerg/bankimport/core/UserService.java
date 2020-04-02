package za.co.joshuabakerg.bankimport.core;

import za.co.joshuabakerg.bankimport.domain.model.SignupRequest;
import za.co.joshuabakerg.bankimport.domain.entities.User;

/**
 * @author Joshua Baker on 17/12/2019
 */
public interface UserService {

    /**
     * Method to create a new user
     *
     * @param request The request that contains the user object
     */
    void createUser(final SignupRequest request);

    /**
     * Methoid to retrieve a user by email address.
     *
     * @param email The requred users email address.
     *
     * @return The user.
     */
    User getByEmail(final String email);

    void clearAll();
}
