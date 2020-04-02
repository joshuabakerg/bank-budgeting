package za.co.joshuabakerg.bankimport.adapter;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import za.co.joshuabakerg.bankimport.domain.model.TransactionString;

/**
 * @author Joshua Baker on 03/03/2020
 */
@Component
public class TransactionStringMapper {

    private static final Pattern DATE_START_PATTER = Pattern.compile("(\\d{1,2}\\s[a-zA-z]{3}\\s\\d{4})(.+)");

    public Collection<TransactionString> execute(final String rawPdfTexr) {
        final Pattern accountPattern = Pattern.compile("(?<account>.+?)\\sstatement");
        final Matcher m = accountPattern.matcher(rawPdfTexr);
        final String account = Optional.of(m)
                .filter(Matcher::find)
                .map(matcher -> matcher.group("account"))
                .get();
        final Collection<String> transactions = rawTransactions(rawPdfTexr);

        final Pattern LINE_PATTERN = Pattern.compile("(?<date>\\d{1,2}\\s[a-zA-z]{3}\\s\\d{4})(?<details>.+?)(?<amount>(-\\s|\\s)R\\d.*\\.\\d+$)");

        final List<TransactionString> transactionStrings = transactions.stream()
                .map(LINE_PATTERN::matcher)
                .filter(Matcher::find)
                .map(matcher -> TransactionString.builder()
                        .account(account)
                        .date(matcher.group("date"))
                        .details(matcher.group("details"))
                        .amount(matcher.group("amount"))
                        .build())
                .collect(Collectors.toList());
        System.out.println(String.format("Account[%s] before[%d] after[%d]", account, transactions.size(), transactionStrings.size()));
        return transactionStrings;
    }

    private Collection<String> rawTransactions(final String rawPdfTexr) {
        final String[] lines = rawPdfTexr.split("\r\n");

        final ArrayList<String> transactions = new ArrayList<>();

        for (String line : lines) {
            final Matcher matcher = DATE_START_PATTER.matcher(line);
            if (matcher.matches()) {
                transactions.add(line);
            } else if (transactions.size() > 0) {
                final String transaction = transactions.get(transactions.size() - 1);
                if (!transaction.matches(".+R\\d.*\\.\\d+$")) {
                    transactions.set(transactions.size() - 1, transaction + " " + line);
                }
            }
        }
        return transactions;
    }

}
