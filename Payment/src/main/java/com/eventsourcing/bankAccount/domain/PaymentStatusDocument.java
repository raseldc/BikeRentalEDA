package com.eventsourcing.bankAccount.domain;

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
@Document(collection = "Payment")
public class PaymentStatusDocument {

    @BsonProperty(value = "_id")
    private String id;

    private Status status;
    private int amount;
    private String reference;

    public enum Status {

        PENDING, APPROVED, REJECTED
    }
}
