package com.transport.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.transport.entity.Driver;
import com.transport.entity.DriverAdvance;

public record DriverResponse(
		Long id,
        String name,
        String phone,
        String address,
        String licenseNo,
        LocalDate joiningDate,
        BigDecimal monthlySalary,
        Long assignedVehicleId,
        boolean onLeave,
        LocalDateTime createdAt,
        List<DriverAdvance> advances
		
		) {
	
	public static DriverResponse of(Driver d, List<DriverAdvance> advances) {
        return new DriverResponse(d.getId(), d.getName(), d.getPhone(), d.getAddress(),
                d.getLicenseNo(), d.getJoiningDate(), d.getMonthlySalary(),
                d.getAssignedVehicleId(), d.isOnLeave(), d.getCreatedAt(), advances);
    }

}
