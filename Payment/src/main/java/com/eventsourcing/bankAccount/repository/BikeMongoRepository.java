package com.eventsourcing.bankAccount.repository;

import com.eventsourcing.bankAccount.domain.BikeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BikeMongoRepository extends MongoRepository<BikeDocument, String> {

    Optional<BikeDocument> findByAggregateId(String aggregateId);
    Optional<BikeDocument> findByBikeId(String bikeId);

    void deleteByAggregateId(String aggregateId);
}
