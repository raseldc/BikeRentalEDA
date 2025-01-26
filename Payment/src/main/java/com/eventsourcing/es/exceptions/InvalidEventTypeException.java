package com.eventsourcing.es.exceptions;

public class InvalidEventTypeException extends RuntimeException {
    public InvalidEventTypeException() {
    }

    public InvalidEventTypeException(String eventType) {
        super("invalid event type: " + eventType);
    }

    public static class AggregateNotFoundException extends RuntimeException {
        public AggregateNotFoundException() {
            super();
        }

        public AggregateNotFoundException(String aggregateID) {
            super("aggregate not found id:" + aggregateID);
        }
    }
}