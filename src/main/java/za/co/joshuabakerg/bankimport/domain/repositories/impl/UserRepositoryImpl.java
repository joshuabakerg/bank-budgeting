package za.co.joshuabakerg.bankimport.domain.repositories.impl;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import za.co.joshuabakerg.bankimport.domain.entities.User;
import za.co.joshuabakerg.bankimport.domain.repositories.UserRepository;

/**
 * @author Joshua Baker on 02/04/2020
 */
@Repository
@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public User save(final User user) {
        user.setId(UUID.randomUUID().toString());
        return mongoTemplate.save(user, "user");
    }

    @Override
    public User findUserByEmail(final String email) {
        return mongoTemplate.findOne(Query.query(Criteria.where("email").is(email)), User.class, "user");
    }

    @Override
    public void deleteAll() {
        return;
    }

}
