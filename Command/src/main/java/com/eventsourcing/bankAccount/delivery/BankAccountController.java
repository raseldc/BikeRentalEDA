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
@RequestMapping(path = "/api/v1/bank")
@Slf4j
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountCommandService commandService;




    @PostMapping
    public ResponseEntity<String> createBankAccount(@Valid @RequestBody CreateBankAccountRequestDTO dto) {
        final var aggregateID = UUID.randomUUID().toString();
        final var id = commandService.handle(new CreateBankAccountCommand(aggregateID, dto.email(), dto.userName(), dto.address()));
        log.info("Created bank account id: {}", id);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PostMapping(path = "/deposit/{aggregateId}")
    public ResponseEntity<Void> depositAmount(@Valid @RequestBody DepositAmountRequestDTO dto, @PathVariable String aggregateId) {
        commandService.handle(new DepositAmountCommand(aggregateId, dto.amount()));
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/email/{aggregateId}")
    public ResponseEntity<Void> changeEmail(@Valid @RequestBody ChangeEmailRequestDTO dto, @PathVariable String aggregateId) {
        commandService.handle(new ChangeEmailCommand(aggregateId, dto.newEmail()));
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/address/{aggregateId}")
    public ResponseEntity<Void> changeAddress(@Valid @RequestBody ChangeAddressRequestDTO dto, @PathVariable String aggregateId) {
        commandService.handle(new ChangeAddressCommand(aggregateId, dto.newAddress()));
        return ResponseEntity.ok().build();
    }


}
