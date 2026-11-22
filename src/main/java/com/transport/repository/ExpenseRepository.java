package com.transport.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.transport.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long>{

	List<Expense> findByVehicleId(Long vehicleId);
    void deleteByTripId(Long tripId);
}
