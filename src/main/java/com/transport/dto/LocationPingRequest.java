package com.transport.dto;

import jakarta.validation.constraints.NotNull;

public record LocationPingRequest(
    @NotNull Long vehicleId,
    @NotNull Double latitude,
    @NotNull Double longitude,
    Double speedKmh,
    Double heading,
    String status,
    String address
) {}
