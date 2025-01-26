package com.eventsourcing.bankAccount.commands;

public record RentBikeCommand(String aggregateID, String bikeId,String bikeType, String location, String startDate, String endDate) {
}
