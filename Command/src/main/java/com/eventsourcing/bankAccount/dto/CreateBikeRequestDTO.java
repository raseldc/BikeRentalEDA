package com.eventsourcing.bankAccount.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record CreateBikeRequestDTO(
        @NotBlank @Size(min = 2, max = 250) String bikeId,
        @NotBlank @Size(min = 2, max = 250) String bikeType,
        @NotBlank @Size(min = 2, max = 250) String location) {
}
