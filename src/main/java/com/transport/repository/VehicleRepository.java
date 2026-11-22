package com.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.transport.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

	boolean existsByNumberIgnoreCase(String number);
    boolean existsByNumberIgnoreCaseAndIdNot(String number, Long id);
}
