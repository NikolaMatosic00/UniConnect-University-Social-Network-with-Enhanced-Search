package com.matosic.SocialNetwork.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.matosic.SocialNetwork.repository.elastic")
public class ElasticsearchConfig {
	
	 @Bean
	    public ElasticsearchClient elasticsearchClient() {
	        RestClient restClient = RestClient.builder(
	            new HttpHost("localhost", 9200)
	        ).build();

	        RestClientTransport transport = new RestClientTransport(
	            restClient, new JacksonJsonpMapper()
	        );

	        return new ElasticsearchClient(transport);
	    }
//    
//    @Bean
//    public ElasticsearchRestTemplate elasticsearchTemplate() {
//        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
//                .connectedTo("localhost:9200")
//                .build();
//
//        return new ElasticsearchRestTemplate(RestClients.create(clientConfiguration).rest(), elasticsearchConverter());
//    }
//
//    @Bean
//    public ElasticsearchConverter elasticsearchConverter() {
//        return new MappingElasticsearchConverter(new SimpleElasticsearchMappingContext());
//    }
//
//    @Bean
//    public void createIndexes(ElasticsearchRestTemplate template) {
//        createIndexIfNotExists(template, "post");
//        createIndexIfNotExists(template, "usergroup");
//    }
//
//    private void createIndexIfNotExists(ElasticsearchRestTemplate template, String indexName) {
//        IndexOperations indexOps = template.indexOps(IndexCoordinates.of(indexName));
//
//        if (!indexOps.exists()) {
//            Map<String, Object> settings = Map.of(
//                    "analysis", Map.of(
//                            "tokenizer", Map.of(
//                                    "standard_tokenizer", Map.of(
//                                            "type", "standard"
//                                    )
//                            ),
//                            "filter", Map.of(
//                                    "serbian_stop", Map.of(
//                                            "type", "stop",
//                                            "stopwords", "_serbian_"
//                                    ),
//                                    "serbian_keywords", Map.of(
//                                            "type", "keyword_marker",
//                                            "keywords", List.of("example")
//                                    ),
//                                    "serbian_stemmer", Map.of(
//                                            "type", "stemmer",
//                                            "language", "serbian"
//                                    )
//                            ),
//                            "analyzer", Map.of(
//                                    "serbian", Map.of(
//                                            "type", "custom",
//                                            "tokenizer", "standard_tokenizer",
//                                            "filter", List.of("lowercase", "serbian_stop", "serbian_keywords", "serbian_stemmer")
//                                    )
//                            )
//                    )
//            );
//
//            indexOps.create(settings); // Create the index with settings
//            indexOps.putMapping(); // Apply the mappings
//        } else {
//            // Handle case when index already exists
//            System.out.println("Index '" + indexName + "' already exists.");
//        }
//    }
}
