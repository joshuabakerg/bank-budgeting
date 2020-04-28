package za.co.joshuabakerg.bankimport.domain.repositories.impl;

import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;

import lombok.AllArgsConstructor;
import za.co.joshuabakerg.bankimport.domain.model.Transaction;
import za.co.joshuabakerg.bankimport.domain.repositories.TransactionRepository;

/**
 * @author Joshua Baker on 08/04/2020
 */
@Repository
@AllArgsConstructor
public class TransactionRepositoryDelegate implements TransactionRepository {

    private final TransactionRepositoryEs transactionRepositoryEs;
    private final TransactionRepositoryMongo transactionRepositoryMongo;

    @Override
    public void saveAll(final Collection<Transaction> newTransactions) {
        repos().forEach(repo -> repo.saveAll(newTransactions));
    }

    @Override
    public void deletePending(final String user) {
        repos().forEach(repo -> repo.deletePending(user));
    }

    @Override
    public Collection<Transaction> findAll() {
        return transactionRepositoryMongo.findAll();
    }

    @Override
    public Collection<Transaction> findAllByPeriod(final LocalDate start, final LocalDate end, final String user) {
        return transactionRepositoryMongo.findAllByPeriod(start, end, user);
    }

    @Override
    public Collection<Transaction> findAllByPeriodAndCategoryIds(final LocalDate start, final LocalDate end, final Collection<String> categoryIds,
                                                                 final String user) {
        return transactionRepositoryMongo.findAllByPeriodAndCategoryIds(start, end, categoryIds, user);
    }

    private Collection<TransactionRepository> repos() {
        return Arrays.asList(transactionRepositoryEs, transactionRepositoryMongo);
    }
}
