package za.co.joshuabakerg.bankimport.domain.repositories;

import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Repository;

import java.util.Collection;

import lombok.AllArgsConstructor;
import za.co.joshuabakerg.bankimport.domain.entities.CategoryAverage;

/**
 * @author Joshua Baker on 06/04/2020
 */
@Repository
@AllArgsConstructor
public class CategoryAverageRepository extends AbstractESRepository<CategoryAverage> {

    private final RestHighLevelClient client;

    public Collection<CategoryAverage> getAveragesForUser(final String user) {
        final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("userId.keyword", user));
        return search(sourceBuilder);
    }

    @Override
    protected EsRepoConfig<CategoryAverage> getConfig() {
        return EsRepoConfig.<CategoryAverage>builder()
                .client(client)
                .index("category_averages")
                .type("_doc")
                .tClass(CategoryAverage.class)
                .build();
    }
}
