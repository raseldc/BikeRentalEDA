package com.eventsourcing.bankAccount.delivery;

import com.eventsourcing.bankAccount.commands.BikeCommandService;
import com.eventsourcing.bankAccount.commands.CreateBikeCommand;
import com.eventsourcing.bankAccount.commands.RentBikeCommand;
import com.eventsourcing.bankAccount.dto.RentBikeRequestDTO;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import io.orkes.conductor.client.WorkflowClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/bike-rent")
@Slf4j
@RequiredArgsConstructor
public class BikeRentController {


    private final WorkflowClient workflowClient;

    private final BikeCommandService commandService;

        @PostMapping
        public ResponseEntity<String> rentBike(@Valid @RequestBody RentBikeRequestDTO dto) {
            final var aggregateID = UUID.randomUUID().toString();
            final var id = commandService.handle(new RentBikeCommand(aggregateID, dto.bikeId(), dto.bikeType(), dto.location(), dto.startDate(), dto.endDate()));
            log.info("Rented bike id: {}", id);
            return ResponseEntity.status(HttpStatus.CREATED).body(id);
        }

//
//    @PostMapping
//    public ResponseEntity<String> rentBikeWorkFlow(@Valid @RequestBody RentBikeRequestDTO dto) {
//        final var aggregateID = UUID.randomUUID().toString();
//
//        StartWorkflowRequest request = new StartWorkflowRequest();
//        request.setName("bike_rent_workflow");
//        request.setVersion(1);
//        request.setCorrelationId(aggregateID);
//
//        Map<String, Object> inputData = new HashMap<>();
//        inputData.put("aggregateID", aggregateID);
//        inputData.put("bikeId", dto.bikeId());
//        inputData.put("bikeType", dto.bikeType());
//        inputData.put("location", dto.location());
//        request.setInput(inputData);
//
//
//        String domain ="rasel";
//
//        if (!domain.isEmpty()) {
//            Map<String, String> taskToDomain = new HashMap<>();
//            taskToDomain.put("*", domain);
//            request.setTaskToDomain(taskToDomain);
//        }
//
//        workflowClient.startWorkflow(request);
//
//        log.info("Started bike rent workflow with id: {}", aggregateID);
//        return ResponseEntity.status(HttpStatus.CREATED).body(aggregateID);
//    }

}
