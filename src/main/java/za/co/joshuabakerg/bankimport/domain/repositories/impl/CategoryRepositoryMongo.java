package za.co.joshuabakerg.bankimport.domain.repositories.impl;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import lombok.AllArgsConstructor;
import za.co.joshuabakerg.bankimport.domain.model.Category;
import za.co.joshuabakerg.bankimport.domain.repositories.CategoryRepository;

/**
 * @author Joshua Baker on 08/04/2020
 */
@Repository
@AllArgsConstructor
public class CategoryRepositoryMongo implements CategoryRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Collection<Category> findAll() {
        final List<Category> category = mongoTemplate.findAll(Category.class, "category");
        return category;
    }

    @Override
    public Collection<Category> findAllByGroupId(final String groupId) {
        final Query query = Query.query(Criteria.where("groupId").is(groupId));
        return mongoTemplate.find(query, Category.class, "category");
    }

    @Override
    public void saveAll(final Collection<Category> categories) {
        for (Category category : categories) {
            mongoTemplate.save(category);
        }
    }
}
