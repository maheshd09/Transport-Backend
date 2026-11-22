package com.transport.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.transport.dto.ExpenseRequest;
import com.transport.entity.Expense;
import com.transport.exception.ApiException;
import com.transport.repository.ExpenseRepository;
import com.transport.repository.TripRepository;
import com.transport.repository.VehicleRepository;

@Service
public class ExpenseService {

	private static final Set<String> CATEGORIES =
            Set.of("Fuel", "Toll", "Food", "Maintenance", "Driver Allowance", "Other");

    private final ExpenseRepository expenseRepository;
    private final VehicleRepository vehicleRepository;
    private final TripRepository tripRepository;

    public ExpenseService(ExpenseRepository expenseRepository, VehicleRepository vehicleRepository,
                          TripRepository tripRepository) {
        this.expenseRepository = expenseRepository;
        this.vehicleRepository = vehicleRepository;
        this.tripRepository = tripRepository;
    }

    public List<Expense> list() {
        return expenseRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .toList();
    }

    @Transactional
    public Expense save(ExpenseRequest req, Long id) {
        if (req.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApiException("Amount must be greater than 0");
        }
        if (!CATEGORIES.contains(req.category())) {
            throw new ApiException("Invalid category: " + req.category());
        }
        if (req.vehicleId() != null && !vehicleRepository.existsById(req.vehicleId())) {
            throw new ApiException("Vehicle not found");
        }
        if (req.tripId() != null && !tripRepository.existsById(req.tripId())) {
            throw new ApiException("Trip not found");
        }

        Expense e;
        if (id != null) {
            e = expenseRepository.findById(id)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Expense not found"));
        } else {
            e = new Expense();
        }
        e.setCategory(req.category());
        e.setDate(req.date());
        e.setVehicleId(req.vehicleId());
        e.setTripId(req.tripId());
        e.setDescription(req.description());
        e.setAmount(req.amount());
        return expenseRepository.save(e);
    }

    @Transactional
    public void delete(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Expense not found");
        }
        expenseRepository.deleteById(id);
    }
}
