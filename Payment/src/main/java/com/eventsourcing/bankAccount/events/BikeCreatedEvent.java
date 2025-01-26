package com.eventsourcing.bankAccount.events;

import com.eventsourcing.es.BaseEvent;
import lombok.Builder;
import lombok.Data;

@Data
public class BikeCreatedEvent extends BaseEvent {
    public static final String Bike_CREATED_V1 = "Bike_CREATED_V1";

    @Builder
    public BikeCreatedEvent(String aggregateId, String bikeId, String bikeType, String location) {
        super(aggregateId);
        this.bikeId = bikeId;
        this.bikeType = bikeType;
        this.location = location;
    }

    private String bikeId;
    private String bikeType;
    private String location;
}