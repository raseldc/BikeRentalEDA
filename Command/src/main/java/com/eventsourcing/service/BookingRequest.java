package com.eventsourcing.service;

import lombok.Data;

@Data
public class BookingRequest {
    private String BookingRequestId;
    private int riderId;
    private String pickUpLocation;
    private String dropOffLocation;
}
