package za.co.joshuabakerg.bankimport.domain.repositories;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import za.co.joshuabakerg.bankimport.config.ApplicationProperties;
import za.co.joshuabakerg.bankimport.domain.model.Category;

/**
 * @author Joshua Baker on 05/03/2020
 */
@Component
@AllArgsConstructor
public class CategoryRepository {

    private final ApplicationProperties applicationProperties;

    public Collection<Category> findAll() {
        return applicationProperties.getCategories();
    }

    public Collection<Category> findAllByIds(Collection<String> ids) {
        return applicationProperties.getCategories().stream()
                .filter(category -> ids.contains(category.getId()))
                .collect(Collectors.toList());
    }

    public Collection<Category> findAllByGroupId(String groupId) {
        return applicationProperties.getCategories().stream()
                .filter(category -> StringUtils.equals(groupId, category.getGroupId()))
                .collect(Collectors.toList());
    }


}
