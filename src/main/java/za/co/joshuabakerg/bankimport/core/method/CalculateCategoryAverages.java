package za.co.joshuabakerg.bankimport.core.method;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import za.co.joshuabakerg.bankimport.domain.entities.CategoryAverage;
import za.co.joshuabakerg.bankimport.domain.model.Period;
import za.co.joshuabakerg.bankimport.domain.model.Transaction;
import za.co.joshuabakerg.bankimport.domain.repositories.TransactionRepository;
import za.co.joshuabakerg.bankimport.utils.PeriodUtil;

import static java.util.Collections.singleton;

/**
 * @author Joshua Baker on 06/04/2020
 */
@Component
@AllArgsConstructor
public class CalculateCategoryAverages {

    private final TransactionRepository transactionRepositoryMongo;

    public CategoryAverage execute(final String categoryId, final String user) {
        final int months = 3;
        final Collection<Period> periods = PeriodUtil.generatePeriods(25, 24, months + 1);
        periods.remove(periods.iterator().next());
        final List<BigDecimal> amounts = periods.stream()
                .map(period -> transactionRepositoryMongo.findAllByPeriodAndCategoryIds(period.getStart(), period.getEnd(), singleton(categoryId), user))
                .map(transactions -> transactions.stream()
                        .map(Transaction::getAmount)
                        .reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO))
                .collect(Collectors.toList());
        final double total = amounts.stream()
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO)
                .doubleValue();
        return CategoryAverage.builder()
                .categoryId(categoryId)
                .userId(user)
                .average(BigDecimal.valueOf(total / (float) months))
                .build();
    }

}
