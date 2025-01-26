package com.eventsourcing.bankAccount.delivery;

import com.eventsourcing.bankAccount.commands.BikeCommandService;
import com.eventsourcing.bankAccount.commands.CreateBikeCommand;
import com.eventsourcing.bankAccount.commands.RentBikeCommand;
import com.eventsourcing.bankAccount.dto.RentBikeRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/bike-rent")
@Slf4j
@RequiredArgsConstructor
public class BikeRentController {

        private final BikeCommandService commandService;

        @PostMapping
        public ResponseEntity<String> rentBike(@Valid @RequestBody RentBikeRequestDTO dto) {
            final var aggregateID = UUID.randomUUID().toString();
            final var id = commandService.handle(new RentBikeCommand(aggregateID, dto.bikeId(), dto.bikeType(), dto.location(), dto.startDate(), dto.endDate()));
            log.info("Rented bike id: {}", id);
            return ResponseEntity.status(HttpStatus.CREATED).body(id);
        }
}
