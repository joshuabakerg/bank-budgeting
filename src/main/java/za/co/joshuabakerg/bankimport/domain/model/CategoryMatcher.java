package za.co.joshuabakerg.bankimport.domain.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Joshua Baker on 04/03/2020
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryMatcher {

    private String groupId;
    private String categoryId;
    private String details;

    private String regex;

    private BigDecimal greaterThan;
    private BigDecimal lessThan;

    private String type;

    private long priority;

}
