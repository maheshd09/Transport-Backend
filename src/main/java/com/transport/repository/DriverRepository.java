package com.transport.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.transport.entity.Driver;

public interface DriverRepository extends JpaRepository<Driver, Long> {
	
    List<Driver> findByAssignedVehicleId(Long vehicleId);

}
