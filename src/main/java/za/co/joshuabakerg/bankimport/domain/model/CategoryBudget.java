package za.co.joshuabakerg.bankimport.domain.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Joshua Baker on 05/03/2020
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryBudget {

    private String categoryId;

    private BigDecimal spent;

    private BigDecimal budgeted;

    private BigDecimal average;


}
