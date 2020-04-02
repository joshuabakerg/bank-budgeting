package za.co.joshuabakerg.bankimport.domain.model;

import java.math.BigDecimal;
import java.util.Collection;

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
public class GroupBudget {

    private String groupId;

    private BigDecimal spent;

    private BigDecimal budgeted;

    private BigDecimal average;

    private Collection<CategoryBudget> categories;

}
