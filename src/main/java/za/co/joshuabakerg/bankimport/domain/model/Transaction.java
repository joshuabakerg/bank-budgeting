package za.co.joshuabakerg.bankimport.domain.model;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Joshua Baker on 03/03/2020
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {

    private Integer id;

    private String account;
    private String user;

    private LocalDateTime date;
    private String type;
    private String description;
    private String reciever;
    private BigDecimal amount;

    private String categoryId;

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(date)
                .append(amount)
                .toHashCode();
    }
}