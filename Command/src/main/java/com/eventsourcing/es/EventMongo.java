package com.eventsourcing.es;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "events")
public class EventMongo {
    @Id
    private String id;
    private String aggregateId;
    private String eventType;
    private String aggregateType;
    private long version;
    private byte[] data;
    private byte[] metaData;
    private LocalDateTime timeStamp;

    public EventMongo(String eventType, String aggregateType) {
        this.id = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.aggregateType = aggregateType;
        this.timeStamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "EventMongo{" +
                "id='" + id + '\'' +
                ", aggregateId='" + aggregateId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", aggregateType='" + aggregateType + '\'' +
                ", version=" + version +
                ", timeStamp=" + timeStamp +
                ", data=" + new String(data) +
                '}';
    }
}