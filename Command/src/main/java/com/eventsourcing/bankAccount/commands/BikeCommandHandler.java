package com.eventsourcing.bankAccount.commands;

import com.eventsourcing.bankAccount.domain.BankAccountAggregate;
import com.eventsourcing.bankAccount.domain.BikeAggregate;
import com.eventsourcing.bankAccount.domain.BikeDocumentState;
import com.eventsourcing.bankAccount.repository.BikeMongoRepositoryState;
import com.eventsourcing.configuration.MongoService;
import com.eventsourcing.es.EventStoreDB;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.cloud.sleuth.annotation.SpanTag;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@RequiredArgsConstructor
@Slf4j
@Service
public class BikeCommandHandler implements BikeCommandService {
    private final EventStoreDB eventStoreDB;
    private static final String SERVICE_NAME = "microservice";
//        private final BikeMongoRepositoryState bikeMongoRepositoryState;

    @Autowired
    private MongoService mongoService;

    @Override
    @NewSpan
    @Retry(name = SERVICE_NAME)
    @CircuitBreaker(name = SERVICE_NAME)
    public String handle(@SpanTag("command") CreateBikeCommand command) {
        final var aggregate = new BikeAggregate(command.aggregateID());
        aggregate.createBike(command.bikeId(), command.bikeType(), command.location());
        eventStoreDB.save(aggregate);

        final var document = BikeDocumentState.builder()
                .aggregateId(command.aggregateID())
                .bikeId(command.bikeId())
                .bikeType(command.bikeType())
                .location(command.location())
                .build();

//            final var insert = bikeMongoRepositoryState.insert(document);
        try {
//            mongoService.saveToSecondary(document);
            mongoService.saveToPrimary(document);
        } catch (Exception e) {
            log.error("Error saving to mongo: {}", e);
        }
        log.info("(CreateBikeCommand) aggregate: {}", aggregate);
        return aggregate.getId();
    }

    @Override
    @NewSpan
    @Retry(name = SERVICE_NAME)
    @CircuitBreaker(name = SERVICE_NAME)
    public String handle(@SpanTag("command") RentBikeCommand command) {
        final var aggregate = new BikeAggregate(command.aggregateID());
        aggregate.rentBike(command.bikeId(), command.bikeType(), command.location(), command.startDate(), command.endDate());
        bikeStatusUpdate(command.bikeId(), command.location());
        eventStoreDB.save(aggregate);


        log.info("(RentBikeCommand) aggregate: {}", aggregate);
        return aggregate.getId();
    }

    @Override
    @NewSpan
    @Retry(name = SERVICE_NAME)
    @CircuitBreaker(name = SERVICE_NAME)
    public String handle(@SpanTag("command") RentBikeStatusPendingCommand command) {
        final var aggregate = new BikeAggregate(command.aggregateID());
        aggregate.rentBike(command.bikeId(), command.bikeType(), command.location(), command.startDate(), command.endDate());
        bikeStatusUpdate(command.bikeId(), command.location());
        eventStoreDB.save(aggregate);


        log.info("(RentBikeCommand) aggregate: {}", aggregate);
        return aggregate.getId();
    }

    public void bikeStatusUpdate(String bikeId, String status) {


        final var documentOptional = mongoService.getPrimaryMongoTemplate().find(query(where("bikeId").is(bikeId)), BikeDocumentState.class).stream().findFirst();

        if (documentOptional.isEmpty())
           return;

        final var document = documentOptional.get();
        document.setLocation(status);
        mongoService.getPrimaryMongoTemplate().save(document);


    }
}
