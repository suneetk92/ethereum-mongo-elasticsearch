package com.sk.ethereum;

import com.sk.ethereum.service.ElasticsearchService;
import com.sk.ethereum.service.MongoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.sk.ethereum.repository.elasticsearch")
@EnableMongoRepositories(basePackages = "com.sk.ethereum.repository.mongodb")
public class EthereumApplication {

    private final MongoService mongoService;
    private final ElasticsearchService elasticsearchService;

    @Value("${is.mongo}")
    private Boolean isMongo;

    @Value("${is.elasticsearch}")
    private Boolean isElasticsearch;

    @Autowired
    public EthereumApplication(MongoService mongoService, ElasticsearchService elasticsearchService) {
        this.mongoService = mongoService;
        this.elasticsearchService = elasticsearchService;
    }

    public static void main(String[] args) {
        SpringApplication.run(EthereumApplication.class, args);
    }

    @PostConstruct
    private void postConstruct() {
        if (isMongo) {
            mongoService.storeInDB();
        }
        if (isElasticsearch) {
            elasticsearchService.storeInDB();
        }
    }

    @PreDestroy
    private void preDestroy() {
        if (isMongo) {
            mongoService.destroyConnections();
        }
        if (isElasticsearch) {
            elasticsearchService.destroyConnections();
        }
    }
}
