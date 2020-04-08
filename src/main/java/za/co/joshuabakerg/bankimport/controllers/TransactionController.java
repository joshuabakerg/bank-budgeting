package za.co.joshuabakerg.bankimport.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import lombok.AllArgsConstructor;
import za.co.joshuabakerg.bankimport.core.TransactionService;
import za.co.joshuabakerg.bankimport.core.method.ApplyCategory;
import za.co.joshuabakerg.bankimport.core.method.GetExcelTransactions;
import za.co.joshuabakerg.bankimport.domain.entities.User;
import za.co.joshuabakerg.bankimport.domain.model.Category;
import za.co.joshuabakerg.bankimport.domain.model.Group;
import za.co.joshuabakerg.bankimport.domain.model.GroupBudget;
import za.co.joshuabakerg.bankimport.domain.model.Period;
import za.co.joshuabakerg.bankimport.domain.model.Transaction;
import za.co.joshuabakerg.bankimport.domain.repositories.impl.CategoryRepositoryEs;
import za.co.joshuabakerg.bankimport.domain.repositories.GroupRepository;
import za.co.joshuabakerg.bankimport.domain.repositories.impl.TransactionRepositoryEs;

/**
 * @author Joshua Baker on 05/03/2020
 */
@RestController
@AllArgsConstructor
public class TransactionController {

    private TransactionService transactionService;

    private TransactionRepositoryEs transactionRepository;
    private CategoryRepositoryEs categoryRepositoryEs;
    private GroupRepository groupRepository;
    private final ApplyCategory applyCategory;
    private final GetExcelTransactions getExcelTransactions;

    @PostMapping(path = "/transactions/import")
    public ResponseEntity<?> importCoverSheet(@NotNull @Valid MultipartHttpServletRequest request) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final User user = (User) authentication.getPrincipal();

        final Iterator<MultipartFile> files = request.getFileMap().values().iterator();
        final Set<Transaction> allTransactions = new HashSet<>();
        while (files.hasNext()) {
            final MultipartFile transactionFile = files.next();
            Collection<Transaction> transactions = Optional.of(transactionFile)
                    .map(multipartFile -> getBytes(multipartFile))
                    .map(getExcelTransactions::execute)
                    .get();
            transactions.forEach(applyCategory::execute);
            final String account = transactionFile.getOriginalFilename().split("_")[1];
            transactions.forEach(transaction -> {
                transaction.setUser(user.getId());
                transaction.setAccount(account);
            });
            allTransactions.addAll(transactions);
        }
        transactionRepository.deletePending(user.getId());
        transactionRepository.saveAll(allTransactions);
        return ResponseEntity.ok("good");
    }

    private byte[] getBytes(final MultipartFile multipartFile) {
        try {
            return multipartFile.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @GetMapping(path = "/transactions")
    public ResponseEntity<Collection<Transaction>> getTransactions(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
                                                                   @RequestParam(required = false) String groupId,
                                                                   @RequestParam(required = false) String categoryId) {
        final Collection<Transaction> transactions = transactionService.getTransactions(start, end, groupId, categoryId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping(path = "/categories")
    public ResponseEntity<Collection<Category>> getCategories() {
        return ResponseEntity.ok(transactionService.getCategories());
    }

    @GetMapping(path = "/groups")
    public ResponseEntity<Collection<Group>> getGroups() {
        return ResponseEntity.ok(transactionService.getGroups());
    }

    @GetMapping(path = "/groups/{groupId}/budget")
    public ResponseEntity<GroupBudget> getGroupBudget(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
                                                      @PathVariable String groupId) {
        final GroupBudget groupBudget = transactionService.getGroupBudget(start, end, groupId);
        return ResponseEntity.ok(groupBudget);
    }

    @GetMapping(path = "/periods")
    public ResponseEntity<Collection<Period>> getPeriods() {
        return ResponseEntity.ok(transactionService.getPeriods());
    }


}
