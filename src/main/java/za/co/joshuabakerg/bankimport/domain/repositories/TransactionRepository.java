package za.co.joshuabakerg.bankimport.domain.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import za.co.joshuabakerg.bankimport.domain.model.Transaction;

/**
 * @author Joshua Baker on 05/03/2020
 */
@Component
@AllArgsConstructor
@Slf4j
public class TransactionRepository {

    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;

    public void saveNew(final Collection<Transaction> newTransactions) {
        try {
            final BulkRequest request = new BulkRequest();
            for (Transaction newTransaction : newTransactions) {
                request.add(new IndexRequest("transactions")
                        .id(String.valueOf(newTransaction.getId()))
                        .type("_doc")
                        .source(objectMapper.convertValue(newTransaction, LinkedHashMap.class)));
            }
            final BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
            final RestStatus status = response.status();
            if (!RestStatus.OK.equals(status)) {
                throw new RuntimeException("Failed to bulk upload tranasctions");
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Collection<Transaction> findAll() {
        final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        return search(sourceBuilder);
    }

    public Collection<Transaction> findAllByPeriod(final LocalDate start, final LocalDate end) {
        final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.rangeQuery("date")
                .lt(end)
                .gt(start));
        return search(sourceBuilder);
    }

    public Collection<Transaction> findAllByPeriodAndCategoryIds(final LocalDate start, final LocalDate end, final Collection<String> categoryIds) {
        final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("date")
                        .lte(end)
                        .gte(start));
        if (CollectionUtils.isNotEmpty(categoryIds)) {
            queryBuilder.must(QueryBuilders.termsQuery("categoryId.keyword", categoryIds));
        }
        sourceBuilder.query(queryBuilder);
        return search(sourceBuilder);
    }

    private Collection<Transaction> search(final SearchSourceBuilder sourceBuilder) {
        try {
            sourceBuilder.size(1000);
            final SearchRequest searchRequest = new SearchRequest();
            searchRequest.indices("transactions");
            searchRequest.source(sourceBuilder);
            final SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            final SearchHit[] hits = response.getHits().getHits();
            return Arrays.stream(hits)
                    .map(documentFields -> documentFields.getSourceAsMap())
                    .map(stringObjectMap -> objectMapper.convertValue(stringObjectMap, Transaction.class))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
