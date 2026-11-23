package com.transport.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.transport.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Pagination — main expense list
    Page<Expense> findAllByOrderByDateDescCreatedAtDesc(Pageable pageable);

    // Filter by vehicle with pagination
    Page<Expense> findByVehicleIdOrderByDateDesc(Long vehicleId, Pageable pageable);

    // For trip-level expense listing (small list, no pagination needed)
    List<Expense> findByTripIdOrderByDateDesc(Long tripId);

    // For vehicle delete cascade
    List<Expense> findByVehicleId(Long vehicleId);

    // For trip delete cascade
    void deleteByTripId(Long tripId);

    // For reports (we'll filter by date in service layer with smaller dataset)
    List<Expense> findByTripIdIn(List<Long> tripIds);
}
