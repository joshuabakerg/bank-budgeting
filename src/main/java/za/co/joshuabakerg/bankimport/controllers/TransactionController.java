package za.co.joshuabakerg.bankimport.controllers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import za.co.joshuabakerg.bankimport.core.method.ApplyCategory;
import za.co.joshuabakerg.bankimport.core.method.GetExcelTransactions;
import za.co.joshuabakerg.bankimport.domain.entities.User;
import za.co.joshuabakerg.bankimport.domain.model.Category;
import za.co.joshuabakerg.bankimport.domain.model.CategoryBudget;
import za.co.joshuabakerg.bankimport.domain.model.Group;
import za.co.joshuabakerg.bankimport.domain.model.GroupBudget;
import za.co.joshuabakerg.bankimport.domain.model.Period;
import za.co.joshuabakerg.bankimport.domain.model.Transaction;
import za.co.joshuabakerg.bankimport.domain.repositories.CategoryRepository;
import za.co.joshuabakerg.bankimport.domain.repositories.GroupRepository;
import za.co.joshuabakerg.bankimport.domain.repositories.TransactionRepository;

/**
 * @author Joshua Baker on 05/03/2020
 */
@RestController
@AllArgsConstructor
public class TransactionController {

    private TransactionRepository transactionRepository;
    private CategoryRepository categoryRepository;
    private GroupRepository groupRepository;
    private final ApplyCategory applyCategory;
    private final GetExcelTransactions getExcelTransactions;

    @PostMapping(path = "/transactions/import")
    public ResponseEntity<?> importCoverSheet(@NotNull @Valid MultipartHttpServletRequest request) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetails principal = (UserDetails) authentication.getPrincipal();

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
                transaction.setUser(principal.getUsername());
                transaction.setAccount(account);
            });
            allTransactions.addAll(transactions);
        }
        transactionRepository.saveNew(allTransactions);
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
        Collection<String> categoryIds = Collections.emptyList();
        if (groupId != null) {
            categoryIds = categoryRepository.findAllByGroupId(groupId).stream()
                    .map(Category::getId)
                    .collect(Collectors.toSet());
        } else if (categoryId != null) {
            categoryIds = Collections.singleton(categoryId);
        }

        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final Collection<Transaction> transactions = transactionRepository.findAllByPeriodAndCategoryIds(start, end, categoryIds, user.getEmail());
        return ResponseEntity.ok(transactions);
    }

    @GetMapping(path = "/categories")
    public ResponseEntity<Collection<Category>> getCategories() {
        final Collection<Category> categories = categoryRepository.findAll();
        return ResponseEntity.ok(categories);
    }

    @GetMapping(path = "/groups")
    public ResponseEntity<Collection<Group>> getGroups() {
        final Collection<Group> groups = groupRepository.findAll();
        return ResponseEntity.ok(groups);
    }

    @GetMapping(path = "/groups/{groupId}/budget")
    public ResponseEntity<GroupBudget> getGroupBudget(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
                                                      @PathVariable String groupId) {
        final Collection<Category> categories = categoryRepository.findAllByGroupId(groupId);

        final BigDecimal totalBudgeted = categories.stream()
                .map(Category::getBudgeted)
                .reduce((bigDecimal, bigDecimal2) -> bigDecimal.add(bigDecimal2))
                .get();

        final List<String> categoryIds = categories.stream()
                .map(Category::getId)
                .collect(Collectors.toList());

        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        final Collection<Transaction> transactions = transactionRepository.findAllByPeriodAndCategoryIds(start, end, categoryIds, user.getEmail());

        final List<CategoryBudget> categoryBudgets = categories.stream()
                .map(category -> {
                    final BigDecimal categorySpent = transactions.stream()
                            .filter(transaction -> StringUtils.equals(transaction.getCategoryId(), category.getId()))
                            .map(Transaction::getAmount)
                            .reduce((bigDecimal, bigDecimal2) -> bigDecimal.add(bigDecimal2))
                            .orElse(BigDecimal.ZERO)
                            .abs();
                    return CategoryBudget.builder()
                            .categoryId(category.getId())
                            .spent(categorySpent)
                            .budgeted(category.getBudgeted())
                            .build();
                })
                .sorted(Comparator.comparing(CategoryBudget::getSpent).reversed())
                .collect(Collectors.toList());

        final BigDecimal totalSpent = transactions.stream()
                .map(Transaction::getAmount)
                .reduce((bigDecimal, bigDecimal2) -> bigDecimal.add(bigDecimal2))
                .orElse(BigDecimal.ZERO)
                .abs();

        return ResponseEntity.ok(GroupBudget.builder()
                .groupId(groupId)
                .spent(totalSpent)
                .budgeted(totalBudgeted)
                .categories(categoryBudgets)
                .build());
    }

    @GetMapping(path = "/periods")
    public ResponseEntity<Collection<Period>> getPeriods() {
        final ArrayList<Period> periods = new ArrayList<>();
        final LocalDate start = LocalDate.now()
                .minusMonths(1)
                .withDayOfMonth(25);
        final LocalDate end = LocalDate.now()
                .withDayOfMonth(24);
        periods.add(Period.builder()
                .name(buildName(start, end))
                .start(start)
                .end(end)
                .build());

        for (int i = 1; i < 10; i++) {
            final LocalDate newEnd = LocalDate.from(end).minusMonths(i);
            final LocalDate newStart = LocalDate.from(start).minusMonths(i);
            periods.add(Period.builder()
                    .name(buildName(newStart, newEnd))
                    .start(newStart)
                    .end(newEnd)
                    .build());
        }


        return ResponseEntity.ok(periods);
    }

    private String buildName(final LocalDate start, final LocalDate end) {
        final String startString = String.format("%d %s %d", start.getDayOfMonth(), start.getMonth().name(), start.getYear());
        final String endString = String.format("%d %s %d", end.getDayOfMonth(), end.getMonth().name(), end.getYear());
        return String.format("%s - %s", startString, endString);
    }

}
