package za.co.joshuabakerg.bankimport.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Joshua Baker on 02/04/2020
 */
@Configuration
public class ElasticsearchConfig {

    @Value("${ES_USER}")
    private String user;

    @Value("${ES_PASSWORD}")
    private String password;

    @Value("${ES_HOST}")
    private String host;

    @Bean
    public RestHighLevelClient restClientBuilder() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, password));
        final HttpHost httpHost = HttpHost.create(host);
        final RestClientBuilder builder = RestClient.builder(httpHost)
                .setHttpClientConfigCallback(httpBuilder -> httpBuilder.setDefaultCredentialsProvider(credentialsProvider));
        return new RestHighLevelClient(builder);
    }

}
