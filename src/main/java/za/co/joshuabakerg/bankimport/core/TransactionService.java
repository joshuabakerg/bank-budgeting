package za.co.joshuabakerg.bankimport.core;

import java.time.LocalDate;
import java.util.Collection;

import za.co.joshuabakerg.bankimport.domain.model.Category;
import za.co.joshuabakerg.bankimport.domain.model.Group;
import za.co.joshuabakerg.bankimport.domain.model.GroupBudget;
import za.co.joshuabakerg.bankimport.domain.model.Period;
import za.co.joshuabakerg.bankimport.domain.model.Transaction;

/**
 * @author Joshua Baker on 06/04/2020
 */
public interface TransactionService {

    Collection<Transaction> getTransactions(LocalDate start, LocalDate end, String groupId, String categoryId);

    Collection<Category> getCategories();

    GroupBudget getGroupBudget(LocalDate start, LocalDate end, String groupId);

    Collection<Group> getGroups();

    Collection<Period> getPeriods();

}
