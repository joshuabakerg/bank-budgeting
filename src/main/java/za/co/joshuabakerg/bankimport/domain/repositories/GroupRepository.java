package za.co.joshuabakerg.bankimport.domain.repositories;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;

import lombok.AllArgsConstructor;
import za.co.joshuabakerg.bankimport.config.ApplicationProperties;
import za.co.joshuabakerg.bankimport.domain.model.Group;

/**
 * @author Joshua Baker on 05/03/2020
 */
@Component
@AllArgsConstructor
public class GroupRepository {

    private final ApplicationProperties applicationProperties;

    public Collection<Group> findAll() {
        return applicationProperties.getGroups();
    }

    public Group findById(final String id){
        return applicationProperties.getGroups().stream()
                .filter(group -> StringUtils.equalsIgnoreCase(id, group.getId()))
                .findFirst()
                .get();
    }


}
