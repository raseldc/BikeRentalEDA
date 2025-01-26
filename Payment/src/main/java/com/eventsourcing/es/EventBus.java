package com.eventsourcing.es;

import org.springframework.cloud.sleuth.annotation.SpanTag;

import java.util.List;

public interface EventBus {
    void publish(List<Event> events);
    public void publishSaga(@SpanTag("events") List<Event> events);
}