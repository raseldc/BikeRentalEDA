package com.eventsourcing.bankAccount.domain;

import com.eventsourcing.es.RentalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "bikes")
public class BikeDocument {

    @BsonProperty(value = "_id")
    private String id;

    @BsonProperty(value = "aggregateId")
    private String aggregateId;

    @BsonProperty(value = "bikeId")
    private String bikeId;

    @BsonProperty(value = "bikeType")
    private String bikeType;

    @BsonProperty(value = "location")
    private String location;

    @BsonProperty(value = "status")
    private RentalStatus status;

}
