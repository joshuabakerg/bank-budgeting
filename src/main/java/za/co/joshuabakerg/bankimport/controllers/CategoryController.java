package za.co.joshuabakerg.bankimport.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import za.co.joshuabakerg.bankimport.core.method.CalculateCategoryAverages;
import za.co.joshuabakerg.bankimport.core.method.UpdateCategoryAverages;
import za.co.joshuabakerg.bankimport.domain.entities.CategoryAverage;
import za.co.joshuabakerg.bankimport.domain.entities.User;

/**
 * @author Joshua Baker on 05/03/2020
 */
@RestController
@AllArgsConstructor
public class CategoryController {

    private CalculateCategoryAverages calculateCategoryAverages;
    private UpdateCategoryAverages updateCategoryAverages;

    @GetMapping(path = "/categories/{categoryId}/average")
    public ResponseEntity<CategoryAverage> importCoverSheet(@PathVariable final String categoryId) {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final CategoryAverage categoryAverage = calculateCategoryAverages.execute(categoryId, user.getId());
        return ResponseEntity.ok(categoryAverage);
    }

    @GetMapping(path = "/categories/updateAverages")
    public ResponseEntity updateAverages() {
        final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        updateCategoryAverages.execute(user.getId());
        return ResponseEntity.ok().build();
    }

}
