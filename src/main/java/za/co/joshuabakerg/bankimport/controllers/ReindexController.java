package za.co.joshuabakerg.bankimport.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import za.co.joshuabakerg.bankimport.config.ApplicationProperties;
import za.co.joshuabakerg.bankimport.core.method.ApplyCategory;
import za.co.joshuabakerg.bankimport.domain.entities.CategoryLimit;
import za.co.joshuabakerg.bankimport.domain.entities.User;
import za.co.joshuabakerg.bankimport.domain.model.Category;
import za.co.joshuabakerg.bankimport.domain.model.Group;
import za.co.joshuabakerg.bankimport.domain.model.Transaction;
import za.co.joshuabakerg.bankimport.domain.repositories.CategoryLimitRepository;
import za.co.joshuabakerg.bankimport.domain.repositories.CategoryRepository;
import za.co.joshuabakerg.bankimport.domain.repositories.GroupRepository;
import za.co.joshuabakerg.bankimport.domain.repositories.TransactionRepository;
import za.co.joshuabakerg.bankimport.domain.repositories.impl.TransactionRepositoryEs;

/**
 * @author Joshua Baker on 03/04/2020
 */
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/reindex")
@AllArgsConstructor
public class ReindexController {

    private final TransactionRepositoryEs transactionRepositoryEs;
    private final TransactionRepository transactionRepositoryMongo;
    private final ApplyCategory applyCategory;
    private final ApplicationProperties applicationProperties;
    private final CategoryRepository categoryRepositoryMongo;
    private final CategoryLimitRepository categoryLimitRepository;
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

    @PostMapping(path = "/limits")
    public ResponseEntity reindexLimitis() {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final List<CategoryLimit> limits = applicationProperties.getCategories().stream()
                .map(category -> CategoryLimit.builder()
                        .categoryId(category.getId())
                        .userId(user.getId())
                        .limit(category.getBudgeted())
                        .build())
                .collect(Collectors.toList());

        categoryLimitRepository.saveAll(limits);
        return ResponseEntity.ok().build();
    }

}
