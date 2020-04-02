package za.co.joshuabakerg.bankimport.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Joshua Baker on 03/03/2020
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionString {

    private String account;

    private String date;

    private String details;

    private String amount;

}
