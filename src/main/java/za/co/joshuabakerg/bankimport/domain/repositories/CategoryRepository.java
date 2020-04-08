package za.co.joshuabakerg.bankimport.domain.repositories;

import java.util.Collection;

import za.co.joshuabakerg.bankimport.domain.model.Category;

/**
 * @author Joshua Baker on 08/04/2020
 */
public interface CategoryRepository {

    Collection<Category> findAll();

    Collection<Category> findAllByGroupId(String groupId);

    void saveAll(Collection<Category> categories);

}
