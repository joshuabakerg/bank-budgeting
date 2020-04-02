package za.co.joshuabakerg.bankimport.adapter;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import za.co.joshuabakerg.bankimport.core.method.ApplyCategory;
import za.co.joshuabakerg.bankimport.domain.model.Transaction;
import za.co.joshuabakerg.bankimport.domain.model.TransactionString;

/**
 * @author Joshua Baker on 03/03/2020
 */
@Component
@AllArgsConstructor
public class TransactionMapper {

    private ApplyCategory applyCategory;

    public Transaction map(final TransactionString transactionString) {
        String stringDate = transactionString.getDate();
        if (stringDate.matches("\\d\\s.+")) {
            stringDate = "0" + stringDate;
        }
        final LocalDate date = LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("dd MMM yyyy"));

        final String details = transactionString.getDetails().trim();

        final String stringAmount = transactionString.getAmount()
                .replaceAll("[^\\d.-]", "");
        final BigDecimal amount = new BigDecimal(stringAmount);

//        final String category = applyCategory.execute(details);

        return Transaction.builder()
                .account(transactionString.getAccount())
                .date(null)
                .description(details)
                .amount(amount)
//                .category(category)
                .build();
    }

}