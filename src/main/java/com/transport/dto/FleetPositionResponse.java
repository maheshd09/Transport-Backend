package com.transport.dto;

public record FleetPositionResponse(
    Long vehicleId,
    String vehicleNumber,
    String vehicleType,
    Double latitude,
    Double longitude,
    Double speedKmh,
    Double heading,
    boolean moving,
    String status,
    Long activeTripId,
    String activeTripCode,
    String driverName,
    String pickupLocation,
    String dropLocation,
    String updatedAt
) {}
