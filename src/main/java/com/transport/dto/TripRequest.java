package com.transport.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record TripRequest(
		String vehicleType,
        @NotNull Long customerId,
        String pickupLocation,
        String dropLocation,
        @NotNull LocalDateTime pickupAt,
        @NotNull LocalDateTime deliveryAt,
        String material,
        Double quantity,
        String quantityUnit,
        Double weightTons,
        Long vehicleId,
        Long driverId,
        Double distanceKm,
        BigDecimal ratePerKm,
        String status,
        String notes) {

}
