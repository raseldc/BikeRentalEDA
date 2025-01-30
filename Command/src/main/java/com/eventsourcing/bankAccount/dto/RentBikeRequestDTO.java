package com.eventsourcing.bankAccount.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

public record RentBikeRequestDTO (
        @NotBlank @Size(min = 10, max = 250) String bikeId,
        @NotBlank @Size(min = 10, max = 250) String bikeType,
        @NotBlank @Size(min = 10, max = 250) String location, String startDate, String endDate, String status) {
}
