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

    @org.springframework.data.jpa.repository.Query("SELECT e FROM Expense e WHERE " +
           "(:vehicleId IS NULL OR e.vehicleId = :vehicleId) AND " +
           "(:category IS NULL OR e.category = :category) AND " +
           "(:q IS NULL OR LOWER(e.description) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(e.category) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "ORDER BY e.date DESC, e.createdAt DESC")
    Page<Expense> findWithFilters(@org.springframework.data.repository.query.Param("vehicleId") Long vehicleId,
                                 @org.springframework.data.repository.query.Param("category") String category,
                                 @org.springframework.data.repository.query.Param("q") String q,
                                 Pageable pageable);

    // For trip-level expense listing (small list, no pagination needed)
    List<Expense> findByTripIdOrderByDateDesc(Long tripId);

    // For vehicle delete cascade
    List<Expense> findByVehicleId(Long vehicleId);

    // For trip delete cascade
    void deleteByTripId(Long tripId);

    // For reports (we'll filter by date in service layer with smaller dataset)
    List<Expense> findByTripIdIn(List<Long> tripIds);
}
