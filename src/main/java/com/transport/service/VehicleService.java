package com.transport.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.transport.dto.VehicleRequest;
import com.transport.entity.Driver;
import com.transport.entity.Expense;
import com.transport.entity.Vehicle;
import com.transport.exception.ApiException;
import com.transport.repository.DriverRepository;
import com.transport.repository.ExpenseRepository;
import com.transport.repository.TripRepository;
import com.transport.repository.VehicleRepository;

import jakarta.transaction.Transactional;

@Service
public class VehicleService {

	private final VehicleRepository vehicleRepository;
    private final TripRepository tripRepository;
    private final DriverRepository driverRepository;
    private final ExpenseRepository expenseRepository;

    public VehicleService(VehicleRepository vehicleRepository, TripRepository tripRepository,
                          DriverRepository driverRepository, ExpenseRepository expenseRepository) {
        this.vehicleRepository = vehicleRepository;
        this.tripRepository = tripRepository;
        this.driverRepository = driverRepository;
        this.expenseRepository = expenseRepository;
    }

    private static String norm(String s) {
        return s == null ? "" : s.replaceAll("\\s", "").toLowerCase();
    }

    public List<Vehicle> list() {
        return vehicleRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .toList();
    }

    @Transactional
    public Vehicle save(VehicleRequest req, Long id) {
        String n = norm(req.number());
        vehicleRepository.findAll().stream()
                .filter(v -> norm(v.getNumber()).equals(n) && !v.getId().equals(id))
                .findFirst()
                .ifPresent(v -> {
                    throw new ApiException("Vehicle " + v.getNumber() + " already exists");
                });

        Vehicle v;
        if (id != null) {
            v = vehicleRepository.findById(id)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Vehicle not found"));
        } else {
            v = new Vehicle();
        }
        v.setNumber(req.number().trim());
        v.setType(req.type());
        v.setModel(req.model());
        v.setCapacityValue(req.capacityValue());
        v.setCapacityUnit(req.capacityUnit());
        v.setCapacityTons(req.capacityTons());
        v.setInsuranceExpiry(req.insuranceExpiry());
        v.setRegistrationNo(req.registrationNo());
        v.setRegistrationExpiry(req.registrationExpiry());
        v.setUnderMaintenance(Boolean.TRUE.equals(req.underMaintenance()));
        return vehicleRepository.save(v);
    }

    @Transactional
    public void delete(Long id) {
        long count = tripRepository.countByVehicleId(id);
        if (count > 0) {
            throw new ApiException("This vehicle is linked to " + count
                    + " trip(s) and can't be deleted.");
        }
        List<Driver> drivers = driverRepository.findByAssignedVehicleId(id);
        drivers.forEach(d -> d.setAssignedVehicleId(null));
        driverRepository.saveAll(drivers);

        List<Expense> expenses = expenseRepository.findByVehicleId(id);
        expenses.forEach(e -> e.setVehicleId(null));
        expenseRepository.saveAll(expenses);

        vehicleRepository.deleteById(id);
    }
}
