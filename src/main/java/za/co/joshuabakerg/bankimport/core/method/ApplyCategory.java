package za.co.joshuabakerg.bankimport.core.method;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import za.co.joshuabakerg.bankimport.config.ApplicationProperties;
import za.co.joshuabakerg.bankimport.domain.model.CategoryMatcher;
import za.co.joshuabakerg.bankimport.domain.model.Transaction;

/**
 * @author Joshua Baker on 04/03/2020
 */
@Component
@AllArgsConstructor
public class ApplyCategory {

    private ApplicationProperties applicationProperties;

    public void execute(final Transaction transaction) {
        applicationProperties.getCategoryMatchers().stream()
                .sorted(Comparator.comparing(CategoryMatcher::getPriority))
                .filter(categoryMatcher -> StringUtils.isBlank(categoryMatcher.getType())
                        || StringUtils.equals(transaction.getType(), categoryMatcher.getType()))
                .filter(categoryMatcher -> categoryMatcher.getGreaterThan() == null || transaction.getAmount().compareTo(categoryMatcher.getGreaterThan()) > 0)
                .filter(categoryMatcher -> categoryMatcher.getLessThan() == null || transaction.getAmount().compareTo(categoryMatcher.getLessThan()) < 0)
                .filter(categoryMatcher -> {
                    if(StringUtils.isBlank(categoryMatcher.getRegex())){
                        return true;
                    }
                    final Pattern pattern = Pattern.compile(categoryMatcher.getRegex());
                    final Matcher matcher = pattern.matcher(transaction.getDescription());
                    return matcher.find();
                })
                .findFirst()
                .ifPresent(categoryMatcher -> {
                    transaction.setCategoryId(categoryMatcher.getCategoryId());
                });
    }

}
