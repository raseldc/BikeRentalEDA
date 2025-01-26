package com.eventsourcing.bankAccount.repository;

import com.eventsourcing.bankAccount.domain.BikeDocumentState;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BikeMongoRepositoryState extends MongoRepository<BikeDocumentState, String> {

    Optional<BikeDocumentState> findByAggregateId(String bikeId);

    void deleteByAggregateId(String aggregateId);
}
