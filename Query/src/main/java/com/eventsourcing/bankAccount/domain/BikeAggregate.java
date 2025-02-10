package com.eventsourcing.bankAccount.domain;

import com.eventsourcing.bankAccount.events.BikeCreatedEvent;
import com.eventsourcing.bankAccount.events.BikeRentEvent;
import com.eventsourcing.es.AggregateRoot;
import com.eventsourcing.es.Event;
import com.eventsourcing.es.SerializerUtils;
import com.eventsourcing.es.exceptions.InvalidEventTypeException;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BikeAggregate extends AggregateRoot {


    public static final String AGGREGATE_TYPE = "BikeAggregate";

    public BikeAggregate(String id) {
        super(id, AGGREGATE_TYPE);
    }

    private String bikeId;
    private String bikeType;
    private String location;
    private String startDate;
    private String endDate;
    private String status;


    @Override
    public void when(Event event) {
        switch (event.getEventType()) {
            case BikeCreatedEvent.Bike_CREATED_V1 ->
                    handle(SerializerUtils.deserializeFromJsonBytes(event.getData(), BikeCreatedEvent.class));
            case BikeRentEvent.Bike_Rent_SagaUpdateStatusReadUpdate2 ->
                    handle(SerializerUtils.deserializeFromJsonBytes(event.getData(), BikeRentEvent.class));
            case BikeRentEvent.Bike_Rent_PaymentComplete ->
                    handle(SerializerUtils.deserializeFromJsonBytes(event.getData(), BikeRentEvent.class));
            case BikeRentEvent.Bike_Rent_PaymentCompleteFeedToSaga ->
                    handle(SerializerUtils.deserializeFromJsonBytes(event.getData(), BikeRentEvent.class));

            case BikeRentEvent.Bike_Rent_SagaStart->
                    handle(SerializerUtils.deserializeFromJsonBytes(event.getData(), BikeRentEvent.class));

            default -> throw new InvalidEventTypeException(event.getEventType());
        }
    }

    private void handle(final BikeCreatedEvent event) {
        this.bikeId = event.getBikeId();
        this.bikeType = event.getBikeType();
        this.location = event.getLocation();

    }

    private void handle(final BikeRentEvent event) {
        this.bikeId = event.getBikeId();
        this.bikeType = event.getBikeType();
        this.location = event.getLocation();
        this.status = event.getStatus();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();


    }


    public void createBike(String bikeId, String bikeType, String location) {
        final var data = BikeCreatedEvent.builder()
                .aggregateId(id)
                .bikeId(bikeId)
                .bikeType(bikeType)
                .location(location)
                .build();

        final byte[] dataBytes = SerializerUtils.serializeToJsonBytes(data);
        final var event = this.createEvent(BikeCreatedEvent.Bike_CREATED_V1, dataBytes, null);
        this.apply(event);
    }

    public void rentBikeStatusPendingSucessfulToSaga(String bikeId, String bikeType, String location, String startDate, String endDate) {
        final var data = BikeRentEvent.builder()
                .aggregateId(id)
                .bikeId(bikeId)
                .bikeType(bikeType)
                .location(location)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        final byte[] dataBytes = SerializerUtils.serializeToJsonBytes(data);
        final var event = this.createEvent(BikeRentEvent.Bike_Rent_SagaUpdateStatusReadUpdate2, dataBytes, null);
        this.apply(event);
    }


    @Override
    public String toString() {
        return "BikeAggregate{" +
                "bikeId='" + bikeId + '\'' +
                ", bikeType='" + bikeType + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    public void rentCompletedFeedBacktoSaga(String bikeId, String bikeType, String location, String startDate, String endDate) {
        final var data = BikeRentEvent.builder()
                .aggregateId(id)
                .bikeId(bikeId)
                .bikeType(bikeType)
                .location(location)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        final byte[] dataBytes = SerializerUtils.serializeToJsonBytes(data);
        final var event = this.createEvent(BikeRentEvent.Bike_Rent_PaymentCompleteFeedToSaga, dataBytes, null);
        this.apply(event);
    }

    public  void rentBike(String bikeId, String bikeType, String location,String startDate,String endDate) {
        final var data = BikeRentEvent.builder()
                .aggregateId(id)
                .bikeId(bikeId)
                .bikeType(bikeType)
                .location(location)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        final byte[] dataBytes = SerializerUtils.serializeToJsonBytes(data);

        final var event = this.createEvent(BikeRentEvent.Bike_Rent_SagaStart, dataBytes, null);
        this.apply(event);
    }
}