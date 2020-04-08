package za.co.joshuabakerg.bankimport.domain.repositories.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Collection;

import lombok.AllArgsConstructor;
import za.co.joshuabakerg.bankimport.config.ApplicationProperties;
import za.co.joshuabakerg.bankimport.domain.model.Group;
import za.co.joshuabakerg.bankimport.domain.repositories.GroupRepository;

/**
 * @author Joshua Baker on 05/03/2020
 */
@Repository
@AllArgsConstructor
public class GroupRepositoryAP implements GroupRepository {

    private final ApplicationProperties applicationProperties;

    @Override
    public Collection<Group> findAll() {
        return applicationProperties.getGroups();
    }

    @Override
    public Group findById(final String id){
        return applicationProperties.getGroups().stream()
                .filter(group -> StringUtils.equalsIgnoreCase(id, group.getId()))
                .findFirst()
                .get();
    }

    @Override
    public void saveAll(final Collection<Group> items) {

    }
}
