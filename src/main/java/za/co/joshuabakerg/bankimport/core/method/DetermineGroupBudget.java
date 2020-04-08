package za.co.joshuabakerg.bankimport.core.method;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import za.co.joshuabakerg.bankimport.domain.entities.CategoryAverage;
import za.co.joshuabakerg.bankimport.domain.entities.User;
import za.co.joshuabakerg.bankimport.domain.model.Category;
import za.co.joshuabakerg.bankimport.domain.model.CategoryBudget;
import za.co.joshuabakerg.bankimport.domain.model.GroupBudget;
import za.co.joshuabakerg.bankimport.domain.model.Transaction;
import za.co.joshuabakerg.bankimport.domain.repositories.CategoryAverageRepository;
import za.co.joshuabakerg.bankimport.domain.repositories.CategoryRepository;
import za.co.joshuabakerg.bankimport.domain.repositories.TransactionRepository;

/**
 * @author Joshua Baker on 06/04/2020
 */
@Component
@AllArgsConstructor
@Slf4j
public class DetermineGroupBudget {

    private CategoryAverageRepository categoryAverageRepository;
    private final TransactionRepository transactionRepositoryMongo;
    private final CategoryRepository categoryRepositoryMongo;

    public GroupBudget execute(final LocalDate start, final LocalDate end, final String groupId) {
        final StopWatch stopWatch = StopWatch.createStarted();
        final Collection<Category> categories = categoryRepositoryMongo.findAllByGroupId(groupId);
        log.info("Took [{}] to fetch categories", split(stopWatch));

        final BigDecimal totalBudgeted = categories.stream()
                .map(Category::getBudgeted)
                .reduce((bigDecimal, bigDecimal2) -> bigDecimal.add(bigDecimal2))
                .get();

        final List<String> categoryIds = categories.stream()
                .map(Category::getId)
                .collect(Collectors.toList());

        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        final Collection<Transaction> transactions = transactionRepositoryMongo.findAllByPeriodAndCategoryIds(start, end, categoryIds, user.getId());
        log.info("Took [{}] to fetch transactions", split(stopWatch));

        final Map<String, BigDecimal> averages = categoryAverageRepository.getAveragesForUser(user.getId()).stream()
                .collect(Collectors.toMap(CategoryAverage::getCategoryId, CategoryAverage::getAverage));
        log.info("Took [{}] to fetch averages", split(stopWatch));

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
                            .average(averages.get(category.getId()).abs())
                            .build();
                })
                .sorted(Comparator.comparing(CategoryBudget::getSpent).reversed())
                .collect(Collectors.toList());

        final BigDecimal totalSpent = transactions.stream()
                .map(Transaction::getAmount)
                .reduce((bigDecimal, bigDecimal2) -> bigDecimal.add(bigDecimal2))
                .orElse(BigDecimal.ZERO)
                .abs();

        final BigDecimal averageTotal = categoryBudgets.stream()
                .map(CategoryBudget::getAverage)
                .reduce((bigDecimal, bigDecimal2) -> bigDecimal.add(bigDecimal2))
                .orElse(BigDecimal.ZERO);

        return GroupBudget.builder()
                .groupId(groupId)
                .spent(totalSpent)
                .budgeted(totalBudgeted)
                .average(averageTotal)
                .categories(categoryBudgets)
                .build();
    }

    private long split(final StopWatch stopWatch) {
        final long time = stopWatch.getTime();
        stopWatch.reset();
        stopWatch.start();
        return time;
    }

}
