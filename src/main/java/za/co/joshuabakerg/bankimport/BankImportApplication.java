package za.co.joshuabakerg.bankimport;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import za.co.joshuabakerg.bankimport.core.method.ApplyCategory;
import za.co.joshuabakerg.bankimport.core.method.GetExcelTransactions;
import za.co.joshuabakerg.bankimport.domain.model.Transaction;
import za.co.joshuabakerg.bankimport.domain.repositories.TransactionRepository;

@EnableWebSecurity
@SpringBootApplication
@AllArgsConstructor
public class BankImportApplication {

    private final ApplyCategory applyCategory;
    private final GetExcelTransactions getExcelTransactions;
    private final TransactionRepository transactionRepository;

    public static void main(String[] args) {
        SpringApplication.run(BankImportApplication.class, args);
    }

    /*public void run(final String... args) throws Exception {
        final List<String> files = Arrays.asList("src/main/resources/DiscoveryBank_gold_credit.xlsx",
                "src/main/resources/DiscoveryBank_12517600870_20200226_20200312.xlsx",
                "src/main/resources/DiscoveryBank_saving.xlsx",
                "src/main/resources/DiscoveryBank_gold_transaction.xlsx");
        List<Transaction> transactions = files.stream()
                .map(getExcelTransactions::execute)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        transactions.forEach(applyCategory::execute);
        transactionRepository.saveNew(transactions);
    }*/
}
