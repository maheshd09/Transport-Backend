package com.transport.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.transport.dto.PagedResponse;
import com.transport.dto.TripRequest;
import com.transport.entity.Driver;
import com.transport.entity.Trip;
import com.transport.entity.Vehicle;
import com.transport.exception.ApiException;
import com.transport.repository.CustomerRepository;
import com.transport.repository.DriverRepository;
import com.transport.repository.ExpenseRepository;
import com.transport.repository.TripRepository;
import com.transport.repository.VehicleRepository;

@Service
public class TripService {

    private static final Set<String> STATUSES =
            Set.of("Pending", "Assigned", "In Transit", "Delivered", "Completed", "Cancelled");
    private static final Set<String> FINISHED = Set.of("Completed", "Cancelled", "Delivered");
    private static final List<String> BLOCKING = List.of("Pending", "Assigned", "In Transit");

    private final TripRepository tripRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final ExpenseRepository expenseRepository;

    public TripService(TripRepository tripRepository, CustomerRepository customerRepository,
                       VehicleRepository vehicleRepository, DriverRepository driverRepository,
                       ExpenseRepository expenseRepository) {
        this.tripRepository = tripRepository;
        this.customerRepository = customerRepository;
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
        this.expenseRepository = expenseRepository;
    }

    // ─── Paginated list with optional filters and search ─────────────────────
    @Transactional(readOnly = true)
    public PagedResponse<Trip> list(int page, int size, String status, Long vehicleId, String q) {
        Pageable pageable = PageRequest.of(page, size);

        boolean hasStatus = status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status) && !"__all__".equalsIgnoreCase(status);
        String cleanStatus = hasStatus ? status.trim() : null;
        String cleanQ = (q != null && !q.isBlank()) ? q.trim() : null;

        Page<Trip> result = tripRepository.findWithFilters(cleanStatus, vehicleId, cleanQ, pageable);
        return PagedResponse.of(result);
    }

    public Trip get(Long id) {
        return find(id);
    }

    private Trip find(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Trip not found"));
    }

    // ─── Fix: Use MAX(id) from DB — O(1) with index, no full table scan ─────
    private String nextCode() {
        long maxId = tripRepository.findMaxId();
        return String.format("TRP-%04d", maxId + 1);
    }

    private static boolean overlaps(Trip a, Trip b) {
        return a.getPickupAt().isBefore(b.getDeliveryAt())
                && b.getPickupAt().isBefore(a.getDeliveryAt());
    }

    private void validate(Trip t) {
        if (!t.getDeliveryAt().isAfter(t.getPickupAt())) {
            throw new ApiException("Delivery date must be after the pickup date");
        }
        if (!STATUSES.contains(t.getStatus())) {
            throw new ApiException("Invalid status: " + t.getStatus());
        }
        if (!customerRepository.existsById(t.getCustomerId())) {
            throw new ApiException("Customer not found");
        }

        boolean notFinished = !FINISHED.contains(t.getStatus());
        boolean blocking = BLOCKING.contains(t.getStatus());

        if (("Assigned".equals(t.getStatus()) || "In Transit".equals(t.getStatus()))
                && (t.getVehicleId() == null || t.getDriverId() == null)) {
            throw new ApiException("A " + t.getStatus() + " trip needs both a vehicle and a driver");
        }

        Vehicle v = null;
        if (t.getVehicleId() != null) {
            v = vehicleRepository.findById(t.getVehicleId())
                    .orElseThrow(() -> new ApiException("Vehicle not found"));
            if (notFinished && v.isUnderMaintenance()) {
                throw new ApiException(v.getNumber() + " is under maintenance");
            }
        }
        Driver d = null;
        if (t.getDriverId() != null) {
            d = driverRepository.findById(t.getDriverId())
                    .orElseThrow(() -> new ApiException("Driver not found"));
            if (notFinished && d.isOnLeave()) {
                throw new ApiException(d.getName() + " is on leave");
            }
        }

        // Fix: Only fetch blocking-status trips for specific vehicle/driver — not all trips
        if (blocking) {
            if (v != null) {
                for (Trip o : tripRepository.findByVehicleIdAndStatusIn(v.getId(), BLOCKING)) {
                    if (isConflict(t, o)) {
                        throw new ApiException(v.getNumber() + " is already booked on "
                                + o.getCode() + " during these dates");
                    }
                }
            }
            if (d != null) {
                for (Trip o : tripRepository.findByDriverIdAndStatusIn(d.getId(), BLOCKING)) {
                    if (isConflict(t, o)) {
                        throw new ApiException(d.getName() + " is already booked on "
                                + o.getCode() + " during these dates");
                    }
                }
            }
        }
    }

    private boolean isConflict(Trip t, Trip o) {
        if (t.getId() != null && t.getId().equals(o.getId())) return false;
        return overlaps(t, o);
    }

    private void apply(Trip t, TripRequest r) {
        t.setVehicleType(r.vehicleType());
        t.setCustomerId(r.customerId());
        t.setPickupLocation(r.pickupLocation());
        t.setDropLocation(r.dropLocation());
        t.setPickupAt(r.pickupAt());
        t.setDeliveryAt(r.deliveryAt());
        t.setMaterial(r.material());
        t.setQuantity(r.quantity());
        t.setQuantityUnit(r.quantityUnit());
        t.setWeightTons(r.weightTons());
        t.setVehicleId(r.vehicleId());
        t.setDriverId(r.driverId());
        t.setDistanceKm(r.distanceKm() == null ? 0.0 : r.distanceKm());
        t.setRatePerKm(r.ratePerKm() == null ? BigDecimal.ZERO : r.ratePerKm());
        t.setStatus(r.status() == null || r.status().isBlank() ? "Pending" : r.status());
        t.setNotes(r.notes());
    }

    @Transactional
    public Trip save(TripRequest req, Long id) {
        Trip t;
        if (id != null) {
            t = find(id);
        } else {
            t = new Trip();
            t.setCode(nextCode());
        }
        apply(t, req);
        validate(t);
        return tripRepository.save(t);
    }

    @Transactional
    public Trip setStatus(Long id, String status) {
        Trip t = find(id);
        t.setStatus(status);
        validate(t);
        return tripRepository.save(t);
    }

    @Transactional
    public void delete(Long id) {
        find(id);
        expenseRepository.deleteByTripId(id);
        tripRepository.deleteById(id);
    }
}