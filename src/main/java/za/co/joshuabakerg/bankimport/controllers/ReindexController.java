package za.co.joshuabakerg.bankimport.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.rest.RestStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.LinkedHashMap;

import lombok.AllArgsConstructor;
import za.co.joshuabakerg.bankimport.config.ApplicationProperties;
import za.co.joshuabakerg.bankimport.core.method.ApplyCategory;
import za.co.joshuabakerg.bankimport.domain.model.Category;
import za.co.joshuabakerg.bankimport.domain.model.Group;
import za.co.joshuabakerg.bankimport.domain.model.Transaction;
import za.co.joshuabakerg.bankimport.domain.repositories.TransactionRepository;

/**
 * @author Joshua Baker on 03/04/2020
 */
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/reindex")
@AllArgsConstructor
public class ReindexController {

    private final TransactionRepository transactionRepository;
    private final ApplyCategory applyCategory;
    private final ApplicationProperties applicationProperties;
    private final ObjectMapper objectMapper;
    private final RestHighLevelClient client;

    @PostMapping(path = "/transactions")
    public ResponseEntity reindexTransactions() {
        final Collection<Transaction> transactions = transactionRepository.findAll();
        transactions.forEach(applyCategory::execute);
        transactionRepository.saveNew(transactions);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/groups")
    public ResponseEntity reindexGroups() {
        final Collection<Group> groups = applicationProperties.getGroups();
        try {
            client.indices().delete(new DeleteIndexRequest("groups"), RequestOptions.DEFAULT);
            client.indices().create(new CreateIndexRequest("groups"), RequestOptions.DEFAULT);
            final BulkRequest request = new BulkRequest();
            for (Group group : groups) {
                request.add(new IndexRequest("groups")
                        .id(group.getId())
                        .type("_doc")
                        .source(objectMapper.convertValue(group, LinkedHashMap.class)));
            }
            final BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
            final RestStatus status = response.status();
            if (!RestStatus.OK.equals(status)) {
                throw new RuntimeException("Failed to bulk upload tranasctions");
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/categories")
    public ResponseEntity reindexCategories() {
        final Collection<Category> categories = applicationProperties.getCategories();
        try {
            client.indices().delete(new DeleteIndexRequest("categories"), RequestOptions.DEFAULT);
            client.indices().create(new CreateIndexRequest("categories"), RequestOptions.DEFAULT);
            final BulkRequest request = new BulkRequest();
            for (Category category : categories) {
                request.add(new IndexRequest("categories")
                        .id(category.getId())
                        .type("_doc")
                        .source(objectMapper.convertValue(category, LinkedHashMap.class)));
            }
            final BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
            final RestStatus status = response.status();
            if (!RestStatus.OK.equals(status)) {
                throw new RuntimeException("Failed to bulk upload tranasctions");
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return ResponseEntity.ok().build();
    }

}
