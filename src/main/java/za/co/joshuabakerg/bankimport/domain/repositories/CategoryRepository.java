package za.co.joshuabakerg.bankimport.domain.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import za.co.joshuabakerg.bankimport.config.ApplicationProperties;
import za.co.joshuabakerg.bankimport.domain.model.Category;
import za.co.joshuabakerg.bankimport.domain.model.Transaction;

/**
 * @author Joshua Baker on 05/03/2020
 */
@Component
@AllArgsConstructor
public class CategoryRepository {

    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;

    public Collection<Category> findAll() {
        final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        return search(sourceBuilder);
    }

    public Collection<Category> findAllByGroupId(String groupId) {
        final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("groupId.keyword", groupId));
        return search(sourceBuilder);
    }

    private Collection<Category> search(final SearchSourceBuilder sourceBuilder) {
        try {
            sourceBuilder.size(1000);
            final SearchRequest searchRequest = new SearchRequest();
            searchRequest.indices("categories");
            searchRequest.source(sourceBuilder);
            final SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            final SearchHit[] hits = response.getHits().getHits();
            return Arrays.stream(hits)
                    .map(documentFields -> documentFields.getSourceAsMap())
                    .map(stringObjectMap -> objectMapper.convertValue(stringObjectMap, Category.class))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
