package com.eventsourcing.bankAccount.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RentBikeRequestConductor {
    private String bikeId;
    private String bikeType;
    private String location;
    private String  startDate;
    private String  endDate; private String  status;
}
