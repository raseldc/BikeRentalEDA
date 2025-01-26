package com.eventsourcing.bankAccount.delivery;


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

//    private final BankAccountQueryService queryService;
//
//    @GetMapping("{aggregateId}")
//    public ResponseEntity<BankAccountResponseDTO> getBankAccount(@PathVariable String aggregateId) {
//        final var result = queryService.handle(new GetBankAccountByIDQuery(aggregateId));
//        log.info("Get bank account result: {}", result);
//        return ResponseEntity.ok(result);
//    }
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
