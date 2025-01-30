package com.eventsourcing.es;

import java.util.List;

public interface EventStoreDB {



    <T extends AggregateRoot> void save(final T aggregate);


}
