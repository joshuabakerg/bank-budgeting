package za.co.joshuabakerg.bankimport.core.method;

import com.opencsv.CSVWriter;

import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import za.co.joshuabakerg.bankimport.adapter.TransactionMapper;
import za.co.joshuabakerg.bankimport.adapter.TransactionStringMapper;
import za.co.joshuabakerg.bankimport.domain.model.Transaction;

/**
 * @author Joshua Baker on 04/03/2020
 */
@Component
@AllArgsConstructor
public class GetPdfTransactions {

    private GetPDFText getPDFText;
    private TransactionStringMapper transactionStringMapper;
    private TransactionMapper transactionMapper;

    public void run(final String... args) throws Exception {
        final Stream<String> stream = Stream.of("Transaction-Jan-2020.pdf", "Transaction-Feb-2020.pdf", "Savings-Feb2020.pdf",
                "Credit-Jan-2020.pdf", "Credit-Feb-2020.pdf");
        //        final Stream<String> stream = Stream.of("Credit-Jan-2020.pdf");
        final List<Transaction> transactions = stream
                .map(getPDFText::execute)
                .map(transactionStringMapper::execute)
                .flatMap(Collection::stream)
                .map(transactionMapper::map)
                .collect(Collectors.toList());

        final CSVWriter csvWriter = new CSVWriter(new FileWriter("out.csv"));
        final String[] header = {"Account", "Date", "Details", "Category", "Amount"};
        csvWriter.writeNext(header);
        csvWriter.writeAll(transactions.stream()
                .map(transaction -> {
                    String[] items = {transaction.getAccount(), transaction.getDate().toString(), transaction.getDescription(),
                            transaction.getCategoryId(),
                            transaction.getAmount().toString()};
                    return items;
                })
                .collect(Collectors.toList()));

        csvWriter.writeNext(new String[]{});
        transactions.stream()
                .collect(Collectors.toMap(Transaction::getCategoryId, Transaction::getAmount, (bigDecimal, bigDecimal2) -> bigDecimal.add(bigDecimal2)))
                .entrySet()
                .forEach(entry -> {
                    final String[] item = {entry.getKey(), entry.getValue().toString()};
                    csvWriter.writeNext(item);
                });

        csvWriter.close();

        System.out.println();
    }

}
