package com.eventsourcing.bankAccount.commands;

import org.springframework.cloud.sleuth.annotation.SpanTag;

public interface BikeCommandService {
    String handle(CreateBikeCommand command);
    String handle(RentBikeCommand command);
    public String handle(@SpanTag("command") RentBikeStatusPendingCommand command);
}
