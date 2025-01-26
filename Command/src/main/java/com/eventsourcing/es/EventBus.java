package com.eventsourcing.es;

import org.springframework.cloud.sleuth.annotation.SpanTag;

import java.util.List;

public interface EventBus {
    void publish(List<Event> events);
    void publishSaga(List<Event> events);
     void publishPayment(@SpanTag("events") List<Event> events);
}