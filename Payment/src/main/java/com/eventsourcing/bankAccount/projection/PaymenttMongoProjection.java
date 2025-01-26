package com.eventsourcing.bankAccount.projection;


import com.eventsourcing.bankAccount.domain.BikeAggregate;
import com.eventsourcing.bankAccount.domain.PaymentStatusDocument;
import com.eventsourcing.bankAccount.events.*;
import com.eventsourcing.configuration.MongoService;
import com.eventsourcing.es.Event;
import com.eventsourcing.es.EventStoreDB;
import com.eventsourcing.es.Projection;
import com.eventsourcing.es.SerializerUtils;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymenttMongoProjection implements Projection {

    //    private final BankAccountMongoRepository mongoRepository;
//    private final BikeMongoRepository bikeMongoRepository;
    @Autowired
    private final EventStoreDB eventStoreDB;
    private static final String SERVICE_NAME = "microservice";
    @Autowired
    MongoService mongoService;

    @KafkaListener(topics = {"PaymentTopic"},
            groupId = "${microservice.kafka.groupId}",
            concurrency = "${microservice.kafka.default-concurrency}")
    public void paymentProjectionListener(@Payload byte[] data, ConsumerRecordMetadata meta, Acknowledgment ack) {
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
            final var aggregate = eventStoreDB.load(events.get(0).getAggregateId(), BikeAggregate.class);
//            final var document = BankAccountMapper.bankAccountDocumentFromAggregate(aggregate);

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

            case BikeRentEvent.Bike_Rent_SagaPaymentInitiate ->
                    handle(SerializerUtils.deserializeFromJsonBytes(event.getData(), BikeRentEvent.class));

            default -> log.error("unknown event type: {}", event.getEventType());
        }
    }

    @NewSpan

    private void handle(@SpanTag("event") BikeRentEvent event) {
        log.info("(when) Payment information create: {}, aggregateID: {}", event, event.getAggregateId());


        final var document = PaymentStatusDocument.builder()
                .reference(event.getBikeId())
                .amount(100)
                .status(PaymentStatusDocument.Status.PENDING)
                .build();

        //final var insert = bikeMongoRepository.insert(document);

         mongoService.saveToPrimary(document);


    }



}
