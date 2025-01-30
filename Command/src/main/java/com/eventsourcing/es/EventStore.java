package com.eventsourcing.es;


import com.eventsourcing.configuration.MongoService;
import com.eventsourcing.es.exceptions.AggregateNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.cloud.sleuth.annotation.SpanTag;
import org.springframework.dao.EmptyResultDataAccessException;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

import static com.eventsourcing.es.Constants.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class EventStore implements EventStoreDB {

    public static final int SNAPSHOT_FREQUENCY = 3;
    private static final String SAVE_EVENTS_QUERY = "INSERT INTO events (aggregate_id, aggregate_type, event_type, data, metadata, version, timestamp) values (:aggregate_id, :aggregate_type, :event_type, :data, :metadata, :version, now())";
    private static final String LOAD_EVENTS_QUERY = "SELECT event_id ,aggregate_id, aggregate_type, event_type, data, metadata, version, timestamp FROM events e WHERE e.aggregate_id = :aggregate_id AND e.version > :version ORDER BY e.version ASC";
    private static final String SAVE_SNAPSHOT_QUERY = "INSERT INTO snapshots (aggregate_id, aggregate_type, data, metadata, version, timestamp) VALUES (:aggregate_id, :aggregate_type, :data, :metadata, :version, now()) ON CONFLICT (aggregate_id) DO UPDATE SET data = :data, version = :version, timestamp = now()";
    private static final String HANDLE_CONCURRENCY_QUERY = "SELECT aggregate_id FROM events e WHERE e.aggregate_id = :aggregate_id LIMIT 1 FOR UPDATE";
    private static final String LOAD_SNAPSHOT_QUERY = "SELECT aggregate_id, aggregate_type, data, metadata, version, timestamp FROM snapshots s WHERE s.aggregate_id = :aggregate_id";
    private static final String EXISTS_QUERY = "SELECT aggregate_id FROM events WHERE e e.aggregate_id = :aggregate_id";

   // private final NamedParameterJdbcTemplate jdbcTemplate;
    private final EventBus eventBus;


    @Autowired
    MongoService mongoService;
    @Override
    @Transactional
    @NewSpan
    public <T extends AggregateRoot> void save(@SpanTag("aggregate") T aggregate) {
        final List<Event> aggregateEvents = new ArrayList<>(aggregate.getChanges());
        eventBus.publish(aggregateEvents);

        mongoService.getSecondaryMongoTemplate().save(convertEventToEventMongo(aggregate.getChanges().get(0)));

        log.info("(save) saved aggregate: {}", aggregate);
    }
    private  EventMongo convertEventToEventMongo(Event e)
    {
        EventMongo eventMongo = new EventMongo();
        eventMongo.setAggregateId(e.getAggregateId());
        eventMongo.setAggregateType(e.getAggregateType());
        eventMongo.setData(e.getData());
        eventMongo.setEventType(e.getEventType());
        eventMongo.setMetaData(e.getMetaData());
        eventMongo.setVersion(e.getVersion());
        eventMongo.setTimeStamp(e.getTimeStamp());
        return eventMongo;
    }


}
