package za.co.joshuabakerg.bankimport.domain.repositories;

import java.util.Collection;

import za.co.joshuabakerg.bankimport.config.ApplicationProperties;
import za.co.joshuabakerg.bankimport.domain.model.Group;

/**
 * @author Joshua Baker on 05/03/2020
 */
public interface GroupRepository {

    Collection<Group> findAll();

    Group findById(final String id);

    void saveAll(Collection<Group> items);

}
