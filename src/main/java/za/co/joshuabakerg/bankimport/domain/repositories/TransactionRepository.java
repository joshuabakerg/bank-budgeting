package za.co.joshuabakerg.bankimport.domain.repositories;

import java.time.LocalDate;
import java.util.Collection;

import za.co.joshuabakerg.bankimport.domain.model.Transaction;

/**
 * @author Joshua Baker on 05/03/2020
 */
public interface TransactionRepository {

    void saveAll(final Collection<Transaction> newTransactions);

    void deletePending(String user);

    Collection<Transaction> findAll();

    Collection<Transaction> findAllByPeriod(LocalDate start, LocalDate end, String user);

    Collection<Transaction> findAllByPeriodAndCategoryIds(LocalDate start, LocalDate end, Collection<String> categoryIds, String user);

}
