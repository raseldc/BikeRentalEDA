package com.eventsourcing.workers;

import com.eventsourcing.bankAccount.commands.RentBikeCommand;
import com.eventsourcing.bankAccount.domain.BikeAggregate;
import com.eventsourcing.bankAccount.domain.BikeDocumentState;
import com.eventsourcing.bankAccount.dto.RentBikeRequestDTO;
import com.eventsourcing.configuration.MongoService;
import com.eventsourcing.es.RentalStatus;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import com.netflix.conductor.sdk.workflow.task.WorkerTask;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@AllArgsConstructor
@Component
public class ConductorWorkers {
    @Autowired
    MongoService mongoService;
    @WorkerTask(value = "state_bd_update", threadCount = 3, pollingInterval = 300)
    public void bikeStatusUpdate(String bikeId, RentalStatus status) {


        final var documentOptional = mongoService.getPrimaryMongoTemplate().find(query(where("bikeId").is(bikeId)), BikeDocumentState.class).stream().findFirst();

        if (documentOptional.isEmpty())
            return;

        final var document = documentOptional.get();
        document.setStatus(status);
        mongoService.getPrimaryMongoTemplate().save(document);


    }


    @WorkerTask(value = "query_db_pending", threadCount = 3, pollingInterval = 300)
    public TaskResult checkForBookingRideTask(RentBikeRequestDTO rentBikeRequestDTO) {
        final var aggregateID = UUID.randomUUID().toString();
        RentBikeCommand   command= new RentBikeCommand(aggregateID, rentBikeRequestDTO.bikeId(), rentBikeRequestDTO.bikeType(), rentBikeRequestDTO.location(), rentBikeRequestDTO.startDate(),
                rentBikeRequestDTO.endDate());

        final var aggregate = new BikeAggregate(command.aggregateID());
        aggregate.rentBike(command.bikeId(), command.bikeType(), command.location(), command.startDate(), command.endDate());
        var event = aggregate.getChanges();


        TaskResult result = new TaskResult();
        Map<String, Object> output = new HashMap<>();
        result.addOutputData("events", event);

        return result;
    }
}
