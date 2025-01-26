package com.eventsourcing.bankAccount.commands;

public record CreateBikeCommand(String aggregateID, String bikeId, String bikeType, String location) {
}
