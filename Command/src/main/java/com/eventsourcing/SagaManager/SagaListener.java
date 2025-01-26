package com.eventsourcing.SagaManager;

import com.eventsourcing.bankAccount.domain.BankAccountAggregate;
import com.eventsourcing.bankAccount.domain.BikeAggregate;
import com.eventsourcing.bankAccount.domain.BikeDocumentState;
import com.eventsourcing.bankAccount.events.*;
import com.eventsourcing.configuration.MongoService;
import com.eventsourcing.es.*;
import com.eventsourcing.mappers.BankAccountMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.cloud.sleuth.annotation.SpanTag;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
@Slf4j
@RequiredArgsConstructor
public class SagaListener implements Projection {
    private static final String SERVICE_NAME = "microservice";
    @Autowired
    private final EventStoreDB eventStoreDB;

    @Autowired
    MongoService mongoService;
    @KafkaListener(topics = "SagaTopic", groupId = "es_microservice")
    public void bankAccountMongoProjectionListener(@Payload byte[] data, ConsumerRecordMetadata meta, Acknowledgment ack) {
        log.info("(BankAccountMongoProjection) topic: {}, offset: {}, partition: {}, timestamp: {}, data: {}", meta.topic(), meta.offset(), meta.partition(), meta.timestamp(), new String(data));

        try {
            final Event[] events = SerializerUtils.deserializeEventsFromJsonBytes(data);
            this.processEvents(Arrays.stream(events).toList());
            ack.acknowledge();
            log.info("ack events: {}", Arrays.toString(events));
        } catch (Exception ex) {
            ack.nack(100);
            log.error("(BankAccountMongoProjection) topic: {}, offset: {}, partition: {}, timestamp: {}", meta.topic(), meta.offset(), meta.partition(), meta.timestamp(), ex);
        }
    }
    @NewSpan
    private void processEvents(@SpanTag("events") List<Event> events) {
        if (events.isEmpty()) return;

        try {
            events.forEach(this::when);
        } catch (Exception ex) {

            log.info("(processEvents) saved document: {}");
        }
    }
    @Override
    @NewSpan
    @Retry(name = SERVICE_NAME)
    @CircuitBreaker(name = SERVICE_NAME)
    public void when(@SpanTag("event") Event event) {
        final var aggregateId = event.getAggregateId();
        log.info("(when) >>>>> aggregateId: {}", aggregateId);

        switch (event.getEventType()) {

            case BikeRentEvent.Bike_Rent_SagaStart ->
                    handle(SerializerUtils.deserializeFromJsonBytes(event.getData(), BikeRentEvent.class));
            case BikeRentEvent.Bike_Rent_SagaUpdateStatusReadUpdate2 ->
                    handlePaymentInitiate(SerializerUtils.deserializeFromJsonBytes(event.getData(), BikeRentEvent.class));
            case BikeRentEvent.Bike_Rent_PaymentComplete ->
                    handlePaymentComplete(SerializerUtils.deserializeFromJsonBytes(event.getData(), BikeRentEvent.class));
            case BikeRentEvent.Bike_Rent_PaymentCompleteFeedToSaga ->
                    handleCompleSaga(SerializerUtils.deserializeFromJsonBytes(event.getData(), BikeRentEvent.class));

            default -> log.error("unknown event type: {}", event.getEventType());
        }
    }

    private void handleCompleSaga(BikeRentEvent bikeRentEvent) {
        log.info("-------------------done done done--------------------------");
    }

    @NewSpan
    private void handlePaymentComplete(@SpanTag("event") BikeRentEvent bikeRentEvent) {
        log.info("(when) BikeAccountCreatedEvent: {}, aggregateID: {}", bikeRentEvent, bikeRentEvent.getAggregateId());

        final var document = BikeRentEvent.builder()
                .aggregateId(bikeRentEvent.getAggregateId())
                .bikeId(bikeRentEvent.getBikeId())
                .bikeType(bikeRentEvent.getBikeType())
                .location(bikeRentEvent.getLocation())

                .build();
        final var aggregateID = UUID.randomUUID().toString();
        final var aggregate = new BikeAggregate(aggregateID);
        aggregate.rentComplete(document.getBikeId(), document.getBikeType(), document.getLocation(),
                document.getStartDate(), document.getEndDate());

        eventStoreDB.save(aggregate);
        bikeStatusUpdate(document.getBikeId(), RentalStatus.RENTED);
        log.info("(BikeCreatedEvent) insert: {}");

    }


    //saga orchestra
    @NewSpan
    private void handle(@SpanTag("event") BikeRentEvent event) {

        log.info("(when) BikeAccountCreatedEvent: {}, aggregateID: {}", event, event.getAggregateId());

        final var document = BikeRentEvent.builder()
                .aggregateId(event.getAggregateId())
                .bikeId(event.getBikeId())
                .bikeType(event.getBikeType())
                .location(event.getLocation())

                .build();
        final var aggregateID = UUID.randomUUID().toString();
        final var aggregate = new BikeAggregate(aggregateID);
        aggregate.rentBikeStatusChange1(document.getBikeId(), document.getBikeType(), document.getLocation(),
                document.getStartDate(), document.getEndDate());

        eventStoreDB.save(aggregate);
        bikeStatusUpdate(document.getBikeId(), RentalStatus.RentProcessStart);
        log.info("(BikeCreatedEvent) insert: {}");

    }


    @NewSpan
    private void handlePaymentInitiate(@SpanTag("event") BikeRentEvent event) {

        log.info("(when) BikeAccountCreatedEvent: {}, aggregateID: {}", event, event.getAggregateId());

        final var document = BikeRentEvent.builder()
                .aggregateId(event.getAggregateId())
                .bikeId(event.getBikeId())
                .bikeType(event.getBikeType())
                .location(event.getLocation())

                .build();
        final var aggregateID = UUID.randomUUID().toString();
        final var aggregate = new BikeAggregate(aggregateID);
        aggregate.rentBikePaymentInitiate(document.getBikeId(), document.getBikeType(), document.getLocation(),
                document.getStartDate(), document.getEndDate());

        eventStoreDB.save(aggregate);
        bikeStatusUpdate(document.getBikeId(), RentalStatus.REQUESTED);
        log.info("(BikeCreatedEvent) insert: {}");

    }
    public void bikeStatusUpdate(String bikeId, RentalStatus status) {


        final var documentOptional = mongoService.getPrimaryMongoTemplate().find(query(where("bikeId").is(bikeId)), BikeDocumentState.class).stream().findFirst();

        if (documentOptional.isEmpty())
            return;

        final var document = documentOptional.get();
        document.setStatus(status);
        mongoService.getPrimaryMongoTemplate().save(document);


    }

}
