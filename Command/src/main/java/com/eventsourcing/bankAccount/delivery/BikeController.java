package com.eventsourcing.bankAccount.delivery;

import com.eventsourcing.bankAccount.commands.*;
import com.eventsourcing.bankAccount.dto.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/bike")
@Slf4j
@RequiredArgsConstructor
public class BikeController {

        private final BikeCommandService commandService;

        @PostMapping
        public ResponseEntity<String> createBike(@Valid @RequestBody CreateBikeRequestDTO dto) {
            final var aggregateID = UUID.randomUUID().toString();
            final var id = commandService.handle(new CreateBikeCommand(aggregateID, dto.bikeId(), dto.bikeType(), dto.location()));
            log.info("Created bike id: {}", id);
            return ResponseEntity.status(HttpStatus.CREATED).body(id);
        }


}
