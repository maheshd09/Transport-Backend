package com.transport.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.transport.entity.DriverAdvance;

public interface DriverAdvanceRepository extends JpaRepository<DriverAdvance, Long> {

	 List<DriverAdvance> findByDriverIdOrderByDateDesc(Long driverId);
	    List<DriverAdvance> findByDriverIdIn(List<Long> driverIds);
	    void deleteByDriverId(Long driverId);
}
