package com.transport.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record AdvanceRequest(
		@NotNull LocalDate date,
        @NotNull BigDecimal amount,
        String note
		
		) {

}
