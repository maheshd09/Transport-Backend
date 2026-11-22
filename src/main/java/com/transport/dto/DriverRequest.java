package com.transport.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;

public record DriverRequest(
		@NotBlank String name,
        String phone,
        String address,
        String licenseNo,
        LocalDate joiningDate,
        BigDecimal monthlySalary,
        Long assignedVehicleId,
        Boolean onLeave
		) {

}
