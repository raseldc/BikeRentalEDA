package com.eventsourcing.configuration;

import com.eventsourcing.bankAccount.domain.BikeDocument;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class MongoService
{
    private final MongoTemplate primaryMongoTemplate;
    private final MongoTemplate secondaryMongoTemplate;

    public MongoService(@Qualifier("primaryMongoTemplate") MongoTemplate primaryMongoTemplate,
                        @Qualifier("secondaryMongoTemplate") MongoTemplate secondaryMongoTemplate) {
        this.primaryMongoTemplate = primaryMongoTemplate;
        this.secondaryMongoTemplate = secondaryMongoTemplate;
    }
    public MongoTemplate getPrimaryMongoTemplate() {
        return primaryMongoTemplate;
    }
    public MongoTemplate getSecondaryMongoTemplate() {
        return secondaryMongoTemplate;
    }

    public void saveToPrimary(BikeDocument document) {
        primaryMongoTemplate.save(document);
    }



}
