package za.co.joshuabakerg.bankimport.domain.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Joshua Baker on 06/04/2020
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryAverage {

    private String categoryId;

    private String userId;

    private BigDecimal average;

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(categoryId)
                .append(userId)
                .toHashCode();
    }
}
