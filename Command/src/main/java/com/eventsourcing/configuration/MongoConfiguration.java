package com.eventsourcing.configuration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class MongoConfiguration {

//    private final MongoTemplate mongoTemplate;
//
//    @PostConstruct
//    public void mongoInit() {
//        final var bankAccounts = mongoTemplate.getCollection("bankAccounts");
//        final var aggregateIdIndex = mongoTemplate.indexOps(BankAccountDocument.class).ensureIndex(new Index("aggregateId", Sort.Direction.ASC).unique());
//        final var indexInfo = mongoTemplate.indexOps(BankAccountDocument.class).getIndexInfo();
//        log.info("MongoDB connected, bankAccounts aggregateId index created: {}", indexInfo);
//    }
//@Value(value = "${SPRING_DATA_MONGODB_URI:mongodb://admin:admin@localhost:27017/bikeState?authSource=admin}")
//private String  deaulturi ;
//
//    @Value(value = "${SPRING_DATA_MONGODB_URI:mongodb://admin:admin@localhost:27017/bikeState?authSource=admin}")
//    private String  uriprimary ;
//    @Value(value = "${SPRING_DATA_MONGODB_URI_Event:mongodb://admin:admin@localhost:27017/EventStore?authSource=admin}")
//    private String  urievent ;

    @Value("${spring.data.mongodb.secondary.uri}")
    private String uriEvent;

    @Value("${spring.data.mongodb.primary.uri}")
    private String defaultUri;





    @Bean
    public MongoTemplate mongoTemplate() {
        System.out.println("deaulturi: --------------------->"+defaultUri);
        System.out.flush();
        log.info("deaulturi: --------------------->"+defaultUri);
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(defaultUri));
    }
    @Primary
    @Bean(name = "primaryMongoProperties")
    @ConfigurationProperties(prefix = "spring.data.mongodb.primary")
    public MongoProperties primaryMongoProperties() {
        return new MongoProperties();
    }

    @Bean(name = "secondaryMongoProperties")
    @ConfigurationProperties(prefix = "spring.data.mongodb.secondary")
    public MongoProperties secondaryMongoProperties() {
        return new MongoProperties();
    }

    @Primary
    @Bean(name = "primaryMongoTemplate")
    public MongoTemplate primaryMongoTemplate(@Qualifier("primaryMongoProperties") MongoProperties mongoProperties) {

        MongoClient mongoClient = MongoClients.create(mongoProperties.getUri());
//        return new MongoTemplate(new SimpleMongoClientDatabaseFactory("mongodb://admin:admin@mongo:27017/bikeState?authSource=admin"));
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(defaultUri));

//        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(mongoClient, mongoProperties.getDatabase()));
    }

    @Bean(name = "secondaryMongoTemplate")
    public MongoTemplate secondaryMongoTemplate(@Qualifier("secondaryMongoProperties") MongoProperties mongoProperties) {
        System.out.println("urievent: --------------------->"+uriEvent);
        MongoClient mongoClient = MongoClients.create(mongoProperties.getUri());
//        return new MongoTemplate(new SimpleMongoClientDatabaseFactory("mongodb://admin:admin@mongo:27017/EventStore?authSource=admin"));
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(uriEvent));

//        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(mongoClient, mongoProperties.getDatabase()));
    }
}
