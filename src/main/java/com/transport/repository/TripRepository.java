package com.transport.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.transport.entity.Trip;

public interface TripRepository extends JpaRepository<Trip, Long> {

	long countByCustomerId(Long customerId);
    long countByVehicleId(Long vehicleId);
    long countByDriverId(Long driverId);
    List<Trip> findAllByOrderByCreatedAtDesc();
    
    List<Trip> findByVehicleId(Long vehicleId);
    List<Trip> findByDriverId(Long driverId);
    
}
