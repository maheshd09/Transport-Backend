package com.transport.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.transport.dto.FleetPositionResponse;
import com.transport.dto.LocationPingRequest;
import com.transport.entity.Driver;
import com.transport.entity.Trip;
import com.transport.entity.Vehicle;
import com.transport.entity.VehicleLocation;
import com.transport.repository.DriverRepository;
import com.transport.repository.TripRepository;
import com.transport.repository.VehicleLocationRepository;
import com.transport.repository.VehicleRepository;

@Service
public class TrackingService {

    private final VehicleLocationRepository locationRepository;
    private final VehicleRepository vehicleRepository;
    private final TripRepository tripRepository;
    private final DriverRepository driverRepository;

    public TrackingService(VehicleLocationRepository locationRepository,
                           VehicleRepository vehicleRepository,
                           TripRepository tripRepository,
                           DriverRepository driverRepository) {
        this.locationRepository = locationRepository;
        this.vehicleRepository = vehicleRepository;
        this.tripRepository = tripRepository;
        this.driverRepository = driverRepository;
    }

    @Transactional
    public VehicleLocation recordPing(LocationPingRequest req) {
        VehicleLocation loc = locationRepository.findByVehicleId(req.vehicleId())
                .orElseGet(() -> {
                    VehicleLocation nl = new VehicleLocation();
                    nl.setVehicleId(req.vehicleId());
                    return nl;
                });
        loc.setLatitude(req.latitude());
        loc.setLongitude(req.longitude());
        loc.setSpeedKmh(req.speedKmh() != null ? req.speedKmh() : 0.0);
        loc.setHeading(req.heading() != null ? req.heading() : 0.0);
        loc.setStatus(req.status() != null ? req.status() : (loc.getSpeedKmh() > 5 ? "Moving" : "Parked"));
        loc.setAddress(req.address());
        loc.setUpdatedAt(LocalDateTime.now());
        return locationRepository.save(loc);
    }

    @Transactional(readOnly = true)
    public List<FleetPositionResponse> getFleetPositions() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        Map<Long, VehicleLocation> locationsByVehicle = locationRepository.findAll().stream()
                .collect(Collectors.toMap(VehicleLocation::getVehicleId, loc -> loc, (a, b) -> a));

        List<Trip> activeTrips = tripRepository.findAll().stream()
                .filter(t -> "In Transit".equals(t.getStatus()) || "Assigned".equals(t.getStatus()))
                .filter(t -> t.getVehicleId() != null)
                .toList();
        Map<Long, Trip> tripByVehicle = activeTrips.stream()
                .collect(Collectors.toMap(Trip::getVehicleId, t -> t, (a, b) -> a));

        Map<Long, Driver> driversById = driverRepository.findAll().stream()
                .collect(Collectors.toMap(Driver::getId, d -> d, (a, b) -> a));

        List<FleetPositionResponse> list = new ArrayList<>();
        for (Vehicle v : vehicles) {
            VehicleLocation loc = locationsByVehicle.get(v.getId());
            Trip trip = tripByVehicle.get(v.getId());

            Double lat = loc != null ? loc.getLatitude() : 22.5726;
            Double lng = loc != null ? loc.getLongitude() : 88.3639;
            Double speed = loc != null ? loc.getSpeedKmh() : (trip != null ? 42.0 : 0.0);
            Double heading = loc != null ? loc.getHeading() : 0.0;
            boolean moving = (loc != null && "Moving".equalsIgnoreCase(loc.getStatus())) || speed > 5 || (trip != null && "In Transit".equals(trip.getStatus()));
            String status = moving ? "Moving" : "Parked";

            String driverName = null;
            if (trip != null && trip.getDriverId() != null) {
                Driver d = driversById.get(trip.getDriverId());
                if (d != null) driverName = d.getName();
            }

            list.add(new FleetPositionResponse(
                    v.getId(),
                    v.getNumber(),
                    v.getType(),
                    lat,
                    lng,
                    speed,
                    heading,
                    moving,
                    status,
                    trip != null ? trip.getId() : null,
                    trip != null ? trip.getCode() : null,
                    driverName,
                    trip != null ? trip.getPickupLocation() : null,
                    trip != null ? trip.getDropLocation() : null,
                    loc != null && loc.getUpdatedAt() != null ? loc.getUpdatedAt().toString() : LocalDateTime.now().toString()
            ));
        }
        return list;
    }

    public Optional<VehicleLocation> getLocation(Long vehicleId) {
        return locationRepository.findByVehicleId(vehicleId);
    }
}
