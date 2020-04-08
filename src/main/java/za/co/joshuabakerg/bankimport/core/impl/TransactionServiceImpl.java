package za.co.joshuabakerg.bankimport.core.impl;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import za.co.joshuabakerg.bankimport.core.TransactionService;
import za.co.joshuabakerg.bankimport.core.method.DetermineGroupBudget;
import za.co.joshuabakerg.bankimport.domain.entities.User;
import za.co.joshuabakerg.bankimport.domain.model.Category;
import za.co.joshuabakerg.bankimport.domain.model.Group;
import za.co.joshuabakerg.bankimport.domain.model.GroupBudget;
import za.co.joshuabakerg.bankimport.domain.model.Period;
import za.co.joshuabakerg.bankimport.domain.model.Transaction;
import za.co.joshuabakerg.bankimport.domain.repositories.CategoryRepository;
import za.co.joshuabakerg.bankimport.domain.repositories.GroupRepository;
import za.co.joshuabakerg.bankimport.domain.repositories.TransactionRepository;
import za.co.joshuabakerg.bankimport.utils.PeriodUtil;

/**
 * @author Joshua Baker on 06/04/2020
 */
@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private DetermineGroupBudget determineGroupBudget;
    private TransactionRepository transactionRepositoryMongo;
    private CategoryRepository categoryRepositoryMongo;
    private GroupRepository groupRepository;

    @Override
    public Collection<Transaction> getTransactions(final LocalDate start, final LocalDate end, final String groupId, final String categoryId) {
        Collection<String> categoryIds = Collections.emptyList();
        if (groupId != null) {
            categoryIds = categoryRepositoryMongo.findAllByGroupId(groupId).stream()
                    .map(Category::getId)
                    .collect(Collectors.toSet());
        } else if (categoryId != null) {
            categoryIds = Collections.singleton(categoryId);
        }

        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return transactionRepositoryMongo.findAllByPeriodAndCategoryIds(start, end, categoryIds, user.getId());
    }

    @Override
    public GroupBudget getGroupBudget(final LocalDate start, final LocalDate end, final String groupId) {
        return determineGroupBudget.execute(start, end, groupId);
    }

    @Override
    public Collection<Category> getCategories() {
        return categoryRepositoryMongo.findAll();
    }

    @Override
    public Collection<Group> getGroups() {
        return groupRepository.findAll();
    }

    @Override
    public Collection<Period> getPeriods() {
        return PeriodUtil.generatePeriods(25, 24, 5);
    }
}
