package za.co.joshuabakerg.bankimport.domain.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Joshua Baker on 05/03/2020
 */
@Component
@AllArgsConstructor
public abstract class AbstractESRepository<T> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Collection<T> findAll() {
        final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        return search(sourceBuilder);
    }

    public void saveAll(final Collection<T> documents) {
        try {
            final BulkRequest request = new BulkRequest();
            for (T document : documents) {
                request.add(new IndexRequest(getConfig().getIndex())
                        .id(getIdentifier(document))
                        .type(getConfig().getType())
                        .source(objectMapper.convertValue(document, LinkedHashMap.class)));
            }
            final BulkResponse response = getConfig().getClient().bulk(request, RequestOptions.DEFAULT);
            final RestStatus status = response.status();
            if (!RestStatus.OK.equals(status)) {
                throw new RuntimeException("Failed to bulk upload " + getConfig().getIndex());
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected Collection<T> search(final SearchSourceBuilder sourceBuilder) {
        try {
            sourceBuilder.size(1000);
            final SearchRequest searchRequest = new SearchRequest();
            searchRequest.indices(getConfig().getIndex());
            searchRequest.source(sourceBuilder);
            final SearchResponse response = getConfig().getClient().search(searchRequest, RequestOptions.DEFAULT);
            final SearchHit[] hits = response.getHits().getHits();
            return Arrays.stream(hits)
                    .map(documentFields -> documentFields.getSourceAsMap())
                    .map(stringObjectMap -> objectMapper.convertValue(stringObjectMap, getConfig().getTClass()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected String getIdentifier(final T document) {
        return String.valueOf(document.hashCode());
    }

    protected abstract EsRepoConfig<T> getConfig();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class EsRepoConfig<T> {

        private String index;

        private String type;

        private RestHighLevelClient client;

        private Class<T> tClass;

    }

}
