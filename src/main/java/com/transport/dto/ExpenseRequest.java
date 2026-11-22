package com.transport.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExpenseRequest(
		
		@NotBlank String category,
        @NotNull LocalDate date,
        Long vehicleId,
        Long tripId,
        String description,
        @NotNull BigDecimal amount) {

}
