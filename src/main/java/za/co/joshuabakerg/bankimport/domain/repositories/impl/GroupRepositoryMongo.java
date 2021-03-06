package za.co.joshuabakerg.bankimport.domain.repositories.impl;

import org.bson.Document;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import lombok.AllArgsConstructor;
import za.co.joshuabakerg.bankimport.domain.model.Group;
import za.co.joshuabakerg.bankimport.domain.repositories.GroupRepository;
import za.co.joshuabakerg.bankimport.utils.DocumentMapper;

/**
 * @author Joshua Baker on 05/03/2020
 */
@Primary
@Repository
@AllArgsConstructor
public class GroupRepositoryMongo implements GroupRepository {

    private final MongoTemplate mongoTemplate;
    private final DocumentMapper documentMapper;

    @Override
    public Collection<Group> findAll() {
        return mongoTemplate.findAll(Group.class, "group");
    }

    @Override
    public Group findById(final String id) {
        return mongoTemplate.findById(id, Group.class, "group");
    }

    @Override
    public void saveAll(final Collection<Group> items) {
        final List<Document> docs = documentMapper.map(items);
        mongoTemplate.getCollection("group")
                .insertMany(docs);
    }


}
