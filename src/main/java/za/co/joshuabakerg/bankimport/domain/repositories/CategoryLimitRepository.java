package za.co.joshuabakerg.bankimport.domain.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import za.co.joshuabakerg.bankimport.domain.entities.CategoryLimit;

/**
 * @author Joshua Baker on 08/04/2020
 */
public interface CategoryLimitRepository extends MongoRepository<CategoryLimit, ObjectId> {
}
