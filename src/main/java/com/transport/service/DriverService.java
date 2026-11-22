package com.transport.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.transport.dto.AdvanceRequest;
import com.transport.dto.DriverRequest;
import com.transport.dto.DriverResponse;
import com.transport.entity.Driver;
import com.transport.entity.DriverAdvance;
import com.transport.exception.ApiException;
import com.transport.repository.DriverAdvanceRepository;
import com.transport.repository.DriverRepository;
import com.transport.repository.TripRepository;

@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private final DriverAdvanceRepository advanceRepository;
    private final TripRepository tripRepository;

    public DriverService(DriverRepository driverRepository, DriverAdvanceRepository advanceRepository,
                         TripRepository tripRepository) {
        this.driverRepository = driverRepository;
        this.advanceRepository = advanceRepository;
        this.tripRepository = tripRepository;
    }

    @Transactional(readOnly = true)
    public List<DriverResponse> list() {
        List<Driver> drivers = driverRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .toList();
        if (drivers.isEmpty()) return List.of();
        Map<Long, List<DriverAdvance>> byDriver = advanceRepository
                .findByDriverIdIn(drivers.stream().map(Driver::getId).toList())
                .stream()
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .collect(Collectors.groupingBy(DriverAdvance::getDriverId));
        return drivers.stream()
                .map(d -> DriverResponse.of(d, byDriver.getOrDefault(d.getId(), List.of())))
                .toList();
    }

    private DriverResponse one(Driver d) {
        return DriverResponse.of(d, advanceRepository.findByDriverIdOrderByDateDesc(d.getId()));
    }

    private Driver find(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Driver not found"));
    }

    @Transactional
    public DriverResponse save(DriverRequest req, Long id) {
        Driver d = id != null ? find(id) : new Driver();
        d.setName(req.name());
        d.setPhone(req.phone());
        d.setAddress(req.address());
        d.setLicenseNo(req.licenseNo());
        d.setJoiningDate(req.joiningDate());
        d.setMonthlySalary(req.monthlySalary() == null ? BigDecimal.ZERO : req.monthlySalary());
        d.setAssignedVehicleId(req.assignedVehicleId());
        d.setOnLeave(Boolean.TRUE.equals(req.onLeave()));
        return one(driverRepository.save(d));
    }

    @Transactional
    public void delete(Long id) {
        find(id);
        long count = tripRepository.countByDriverId(id);
        if (count > 0) {
            throw new ApiException("This driver is linked to " + count
                    + " trip(s) and can't be deleted.");
        }
        advanceRepository.deleteByDriverId(id);
        driverRepository.deleteById(id);
    }

    @Transactional
    public DriverResponse addAdvance(Long driverId, AdvanceRequest req) {
        Driver d = find(driverId);
        if (req.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApiException("Advance amount must be greater than 0");
        }
        DriverAdvance a = new DriverAdvance();
        a.setDriverId(driverId);
        a.setDate(req.date());
        a.setAmount(req.amount());
        a.setNote(req.note());
        advanceRepository.save(a);
        return one(d);
    }

    @Transactional
    public DriverResponse deleteAdvance(Long driverId, Long advanceId) {
        Driver d = find(driverId);
        DriverAdvance a = advanceRepository.findById(advanceId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Advance not found"));
        if (!a.getDriverId().equals(driverId)) {
            throw new ApiException("Advance does not belong to this driver");
        }
        advanceRepository.delete(a);
        return one(d);
    }
}