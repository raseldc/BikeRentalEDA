package com.eventsourcing.bankAccount.delivery;


import com.eventsourcing.bankAccount.domain.BikeAggregate;
import com.eventsourcing.bankAccount.domain.PaymentStatusDocument;
import com.eventsourcing.configuration.MongoService;
import com.eventsourcing.es.EventStoreDB;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@RestController
@RequestMapping(path = "/api/payment")
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

    @Autowired
    MongoService mongoService;

    @Autowired
    private final EventStoreDB eventStoreDB;
    @PostMapping("/confirmPayment")
    public void confirmPayment(@RequestParam(value = "paymentId") String paymentId,
                               @RequestParam(value = "confirmStatus") Boolean confirmStatus) throws JsonProcessingException {

       // final var  paymentStatus =  mongoService.getPrimaryMongoTemplate().find(query(where("paymentId").is(paymentId)), PaymentStatusDocument.class).stream().findFirst();

        final var paymentStatus = mongoService.getPrimaryMongoTemplate().find(query(where("_id").is(paymentId)), PaymentStatusDocument.class).stream().findFirst().get();





        if (confirmStatus != null && confirmStatus) {
            paymentStatus.setStatus(PaymentStatusDocument.Status.APPROVED);
            mongoService.saveToPrimary(paymentStatus);


        } else {
            paymentStatus.setStatus(PaymentStatusDocument.Status.REJECTED);
            mongoService.saveToPrimary(paymentStatus);

        }
        final var aggregateID = UUID.randomUUID().toString();
        final var aggregate = new BikeAggregate(aggregateID);
        aggregate.rentComplete(paymentStatus.getReference(),"", "","","");
        eventStoreDB.save(aggregate);
    }
//
//
//
//    @GetMapping("/balance")
//    public ResponseEntity<Page<BankAccountResponseDTO>> getAllOrderByBalance(@RequestParam(name = "page", defaultValue = "0") Integer page,
//                                                                             @RequestParam(name = "size", defaultValue = "10") Integer size) {
//
//        final var result = queryService.handle(new FindAllOrderByBalance(page, size));
//        log.info("Get all by balance page: {}, size: {}, result: {}", page, size, result);
//        return ResponseEntity.ok(result);
//    }
}
