package com.eventsourcing.bankAccount.commands;

public record RentBikeStatusPendingCommand(String aggregateID, String bikeId, String bikeType, String location, String startDate, String endDate) {
}
