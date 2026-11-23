package com.transport.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.transport.dto.FleetPositionResponse;
import com.transport.dto.LocationPingRequest;
import com.transport.entity.VehicleLocation;
import com.transport.service.TrackingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tracking")
public class TrackingController {

    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @GetMapping("/fleet")
    public List<FleetPositionResponse> getFleetPositions() {
        return trackingService.getFleetPositions();
    }

    @PostMapping("/ping")
    public VehicleLocation recordPing(@Valid @RequestBody LocationPingRequest req) {
        return trackingService.recordPing(req);
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<VehicleLocation> getVehicleLocation(@PathVariable Long vehicleId) {
        return trackingService.getLocation(vehicleId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
