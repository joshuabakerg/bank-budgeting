package za.co.joshuabakerg.bankimport.domain.repositories.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
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

    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;

    @Override
    public User save(final User user) {
        try {
            user.setId(UUID.randomUUID().toString());
            final IndexRequest request = new IndexRequest("users");
            request.type("_doc");
            request.source(objectMapper.convertValue(user, LinkedHashMap.class));
            final IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            final RestStatus status = response.status();
            if (!RestStatus.CREATED.equals(status)) {
                throw new RuntimeException("Failed to bulk upload tranasctions");
            }
            return user;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public User findUserByEmail(final String email) {
        final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("email.keyword", email));
        final Collection<User> users = search(sourceBuilder);
        if (CollectionUtils.isNotEmpty(users)) {
            return users.iterator().next();
        }
        return null;
    }

    @Override
    public void deleteAll() {
        return;
    }

    private Collection<User> search(final SearchSourceBuilder sourceBuilder) {
        try {
            final SearchRequest searchRequest = new SearchRequest();
            searchRequest.indices("users");
            searchRequest.source(sourceBuilder);
            final SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            final SearchHit[] hits = response.getHits().getHits();
            return Arrays.stream(hits)
                    .map(documentFields -> documentFields.getSourceAsMap())
                    .map(stringObjectMap -> objectMapper.convertValue(stringObjectMap, User.class))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
