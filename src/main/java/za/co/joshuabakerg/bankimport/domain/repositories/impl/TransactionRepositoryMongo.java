package za.co.joshuabakerg.bankimport.domain.repositories.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import lombok.AllArgsConstructor;
import za.co.joshuabakerg.bankimport.domain.model.Transaction;
import za.co.joshuabakerg.bankimport.domain.repositories.TransactionRepository;
import za.co.joshuabakerg.bankimport.utils.DocumentMapper;

/**
 * @author Joshua Baker on 08/04/2020
 */
@Repository
@AllArgsConstructor
public class TransactionRepositoryMongo implements TransactionRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public void saveAll(final Collection<Transaction> newTransactions) {
        final List<Document> docs = DocumentMapper.map(newTransactions);
        mongoTemplate.getCollection("transaction")
                .insertMany(docs);
    }

    @Override
    public void deletePending(final String user) {
        final Criteria criteria = Criteria.where("type").is("Pending")
                .and("user").is(user);
        mongoTemplate.remove(Query.query(criteria), "transaction");
    }

    @Override
    public Collection<Transaction> findAll() {
        return mongoTemplate.findAll(Transaction.class, "transaction");
    }

    @Override
    public Collection<Transaction> findAllByPeriod(final LocalDate start, final LocalDate end, final String user) {
        final Criteria criteria = Criteria.where("date").lte(end.toString()).gte(start.toString())
                .and("user").is(user);
        return mongoTemplate.find(Query.query(criteria), Transaction.class, "transaction");
    }

    @Override
    public Collection<Transaction> findAllByPeriodAndCategoryIds(final LocalDate start, final LocalDate end, final Collection<String> categoryIds,
                                                                 final String user) {
        final Criteria criteria = Criteria.where("date").gte(start.toString()).lte(end.toString())
                .and("user").is(user);
        if (CollectionUtils.isNotEmpty(categoryIds)) {
            criteria.and("categoryId").in(categoryIds);
        }
        final Query query = Query.query(criteria);
        return mongoTemplate.find(query, Transaction.class, "transaction");
    }
}
