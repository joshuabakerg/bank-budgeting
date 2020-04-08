package za.co.joshuabakerg.bankimport.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MongoDbConfig extends AbstractMongoClientConfiguration {

    @Value("${dbConnectionString}")
    private String connectionUrl;

    @Override
    public MongoClient mongoClient() {
        return MongoClients.create(connectionUrl);
    }

    @Override
    protected String getDatabaseName() {
        return "bank-import";
    }
}
