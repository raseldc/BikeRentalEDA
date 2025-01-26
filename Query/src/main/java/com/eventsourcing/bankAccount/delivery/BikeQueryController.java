package com.eventsourcing.bankAccount.delivery;

import com.eventsourcing.bankAccount.domain.BikeDocument;
import com.eventsourcing.bankAccount.dto.BankAccountResponseDTO;
import com.eventsourcing.configuration.MongoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@RestController
@Slf4j
public class BikeQueryController {

    final private MongoService mongoService;

    public  BikeQueryController(MongoService mongoService)
    {
        this.mongoService = mongoService;
    }
    @GetMapping("/get-bike-status")
    public ResponseEntity<BikeDocument> getBikByBikeId(@RequestParam(name = "bikeId", defaultValue = "0") String bikeId)
    {

        final var documentOptional = mongoService.getPrimaryMongoTemplate().find(query(where("bikeId").is(bikeId)), BikeDocument.class).stream().findFirst();
        if(documentOptional.isPresent())
        {
            return ResponseEntity.ok(documentOptional.get());
        }
        return ResponseEntity.notFound().build();

    }

    @GetMapping("/get-all-bike")
    public ResponseEntity<List<BikeDocument>> getAllBike()
    {

        final var documentOptional = mongoService.getPrimaryMongoTemplate().findAll(BikeDocument.class).stream().toList();
        if(documentOptional.size()>0)
        {
            return ResponseEntity.ok(documentOptional);
        }

        return ResponseEntity.notFound().build();

    }
}
