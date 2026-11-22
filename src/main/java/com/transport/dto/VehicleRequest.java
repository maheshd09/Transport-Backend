package com.transport.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;

public record VehicleRequest(
		@NotBlank String number,
        @NotBlank String type,
        String model,
        Double capacityValue,
        String capacityUnit,
        Double capacityTons,
        LocalDate insuranceExpiry,
        String registrationNo,
        LocalDate registrationExpiry,
        Boolean underMaintenance
		) {

}
