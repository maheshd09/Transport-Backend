package com.transport.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.transport.entity.VehicleLocation;

@Repository
public interface VehicleLocationRepository extends JpaRepository<VehicleLocation, Long> {
    Optional<VehicleLocation> findByVehicleId(Long vehicleId);
    void deleteByVehicleId(Long vehicleId);
}
