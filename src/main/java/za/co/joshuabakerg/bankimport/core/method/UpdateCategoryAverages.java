package za.co.joshuabakerg.bankimport.core.method;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import za.co.joshuabakerg.bankimport.domain.entities.CategoryAverage;
import za.co.joshuabakerg.bankimport.domain.repositories.CategoryAverageRepository;
import za.co.joshuabakerg.bankimport.domain.repositories.impl.CategoryRepositoryEs;

/**
 * @author Joshua Baker on 06/04/2020
 */
@Component
@AllArgsConstructor
public class UpdateCategoryAverages {

    private CategoryRepositoryEs categoryRepositoryEs;
    private CategoryAverageRepository categoryAverageRepository;
    private CalculateCategoryAverages calculateCategoryAverages;

    public void execute(final String user) {
        final List<CategoryAverage> averages = categoryRepositoryEs.findAll().stream()
                .map(category -> calculateCategoryAverages.execute(category.getId(), user))
                .collect(Collectors.toList());
        categoryAverageRepository.saveAll(averages);
    }

}
