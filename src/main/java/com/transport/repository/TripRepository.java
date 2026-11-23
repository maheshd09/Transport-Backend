package com.transport.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.transport.entity.Trip;

public interface TripRepository extends JpaRepository<Trip, Long> {

    // Pagination with optional filters & search — used by list API
    Page<Trip> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Trip> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    Page<Trip> findByVehicleIdOrderByCreatedAtDesc(Long vehicleId, Pageable pageable);

    Page<Trip> findByStatusAndVehicleIdOrderByCreatedAtDesc(String status, Long vehicleId, Pageable pageable);

    @Query("SELECT t FROM Trip t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:vehicleId IS NULL OR t.vehicleId = :vehicleId) AND " +
           "(:q IS NULL OR LOWER(t.code) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(t.pickupLocation) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(t.dropLocation) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(t.material) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "ORDER BY t.createdAt DESC")
    Page<Trip> findWithFilters(@org.springframework.data.repository.query.Param("status") String status,
                               @org.springframework.data.repository.query.Param("vehicleId") Long vehicleId,
                               @org.springframework.data.repository.query.Param("q") String q,
                               Pageable pageable);

    // For dropdown lookups (no pagination needed — only active trips)
    List<Trip> findByVehicleId(Long vehicleId);
    List<Trip> findByDriverId(Long driverId);

    // Counts
    long countByCustomerId(Long customerId);
    long countByVehicleId(Long vehicleId);
    long countByDriverId(Long driverId);

    // Dashboard — recent trips (limited, no full scan)
    List<Trip> findTop10ByOrderByCreatedAtDesc();

    // Efficient nextCode — just get the max id from DB, O(1) with index
    @Query("SELECT COALESCE(MAX(t.id), 0) FROM Trip t")
    Long findMaxId();

    // Reports — filtered by status (no full scan for dashboard counters)
    long countByStatus(String status);

    // For conflict check — only fetch trips for specific vehicle/driver
    List<Trip> findByVehicleIdAndStatusIn(Long vehicleId, List<String> statuses);
    List<Trip> findByDriverIdAndStatusIn(Long driverId, List<String> statuses);
}
