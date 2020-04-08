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
import za.co.joshuabakerg.bankimport.domain.repositories.CategoryRepository;
import za.co.joshuabakerg.bankimport.domain.repositories.GroupRepository;
import za.co.joshuabakerg.bankimport.domain.repositories.TransactionRepository;

/**
 * @author Joshua Baker on 03/04/2020
 */
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/reindex")
@AllArgsConstructor
public class ReindexController {

    private final TransactionRepository transactionRepositoryEs;
    private final TransactionRepository transactionRepositoryMongo;
    private final ApplyCategory applyCategory;
    private final ApplicationProperties applicationProperties;
    private final CategoryRepository categoryRepositoryMongo;
    private final GroupRepository groupRepository;

    @PostMapping(path = "/transactions")
    public ResponseEntity reindexTransactions() {
        final Collection<Transaction> transactions = transactionRepositoryEs.findAll();
        transactions.forEach(applyCategory::execute);
        transactionRepositoryMongo.saveAll(transactions);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/groups")
    public ResponseEntity reindexGroups() {
        final Collection<Group> groups = applicationProperties.getGroups();
        groupRepository.saveAll(groups);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/categories")
    public ResponseEntity reindexCategories() {
        final Collection<Category> categories = applicationProperties.getCategories();
        categoryRepositoryMongo.saveAll(categories);
        return ResponseEntity.ok().build();
    }

}
