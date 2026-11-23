package com.transport.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.transport.dto.ReportSummaryResponse;
import com.transport.dto.ReportSummaryResponse.GroupSummary;
import com.transport.entity.Customer;
import com.transport.entity.Driver;
import com.transport.entity.Expense;
import com.transport.entity.Trip;
import com.transport.entity.Vehicle;
import com.transport.repository.CustomerRepository;
import com.transport.repository.DriverRepository;
import com.transport.repository.ExpenseRepository;
import com.transport.repository.TripRepository;
import com.transport.repository.VehicleRepository;

@Service
public class ReportService {

    private final TripRepository tripRepository;
    private final ExpenseRepository expenseRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final CustomerRepository customerRepository;

    public ReportService(TripRepository tripRepository,
                         ExpenseRepository expenseRepository,
                         VehicleRepository vehicleRepository,
                         DriverRepository driverRepository,
                         CustomerRepository customerRepository) {
        this.tripRepository = tripRepository;
        this.expenseRepository = expenseRepository;
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
        this.customerRepository = customerRepository;
    }

    private static double round2(double val) {
        return BigDecimal.valueOf(val).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private static double tripRevenue(Trip t) {
        if ("Cancelled".equalsIgnoreCase(t.getStatus())) return 0.0;
        double dist = t.getDistanceKm() != null ? t.getDistanceKm() : 0.0;
        double rate = t.getRatePerKm() != null ? t.getRatePerKm().doubleValue() : 0.0;
        return dist * rate;
    }

    @Transactional(readOnly = true)
    public ReportSummaryResponse generateReport(String start, String end,
                                                Long vehicleId, Long driverId,
                                                Long customerId, String status) {
        List<Trip> allTrips = tripRepository.findAll();
        List<Expense> allExpenses = expenseRepository.findAll();

        LocalDate sDate = (start != null && !start.isBlank()) ? LocalDate.parse(start) : null;
        LocalDate eDate = (end != null && !end.isBlank()) ? LocalDate.parse(end) : null;

        List<Trip> filteredTrips = allTrips.stream()
                .filter(t -> {
                    if (sDate != null && t.getPickupAt() != null && t.getPickupAt().toLocalDate().isBefore(sDate)) return false;
                    if (eDate != null && t.getPickupAt() != null && t.getPickupAt().toLocalDate().isAfter(eDate)) return false;
                    if (vehicleId != null && !vehicleId.equals(t.getVehicleId())) return false;
                    if (driverId != null && !driverId.equals(t.getDriverId())) return false;
                    if (customerId != null && !customerId.equals(t.getCustomerId())) return false;
                    if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status) && !status.equalsIgnoreCase(t.getStatus())) return false;
                    return true;
                })
                .sorted((a, b) -> {
                    if (a.getPickupAt() == null && b.getPickupAt() == null) return 0;
                    if (a.getPickupAt() == null) return -1;
                    if (b.getPickupAt() == null) return 1;
                    return a.getPickupAt().compareTo(b.getPickupAt());
                })
                .toList();

        Set<Long> tripIds = filteredTrips.stream().map(Trip::getId).collect(Collectors.toSet());
        boolean includeGeneral = (driverId == null && customerId == null && (status == null || status.isBlank() || "ALL".equalsIgnoreCase(status)));

        List<Expense> filteredExpenses = allExpenses.stream()
                .filter(e -> {
                    if (e.getTripId() != null) {
                        return tripIds.contains(e.getTripId());
                    }
                    if (!includeGeneral) return false;
                    if (sDate != null && e.getDate() != null && e.getDate().isBefore(sDate)) return false;
                    if (eDate != null && e.getDate() != null && e.getDate().isAfter(eDate)) return false;
                    if (vehicleId != null && !vehicleId.equals(e.getVehicleId())) return false;
                    return true;
                })
                .sorted((a, b) -> {
                    if (a.getDate() == null && b.getDate() == null) return 0;
                    if (a.getDate() == null) return -1;
                    if (b.getDate() == null) return 1;
                    return b.getDate().compareTo(a.getDate());
                })
                .toList();

        double totalRevenue = round2(filteredTrips.stream().mapToDouble(ReportService::tripRevenue).sum());
        double totalExpenses = round2(filteredExpenses.stream().mapToDouble(e -> e.getAmount() != null ? e.getAmount().doubleValue() : 0.0).sum());
        double totalProfit = round2(totalRevenue - totalExpenses);
        double totalDistance = round2(filteredTrips.stream()
                .filter(t -> !"Cancelled".equalsIgnoreCase(t.getStatus()))
                .mapToDouble(t -> t.getDistanceKm() != null ? t.getDistanceKm() : 0.0).sum());

        // Group by Vehicle
        Map<Long, Vehicle> vehicleMap = vehicleRepository.findAll().stream().collect(Collectors.toMap(Vehicle::getId, v -> v, (a, b) -> a));
        List<GroupSummary> byVehicle = new ArrayList<>();
        Map<Long, List<Trip>> tripsByVeh = filteredTrips.stream().filter(t -> t.getVehicleId() != null).collect(Collectors.groupingBy(Trip::getVehicleId));
        for (Map.Entry<Long, List<Trip>> entry : tripsByVeh.entrySet()) {
            Vehicle v = vehicleMap.get(entry.getKey());
            String name = v != null ? (v.getNumber() + " (" + v.getType() + ")") : ("Vehicle #" + entry.getKey());
            double rev = round2(entry.getValue().stream().mapToDouble(ReportService::tripRevenue).sum());
            Set<Long> vTripIds = entry.getValue().stream().map(Trip::getId).collect(Collectors.toSet());
            double exp = round2(filteredExpenses.stream()
                    .filter(e -> (e.getTripId() != null && vTripIds.contains(e.getTripId())) || (e.getTripId() == null && entry.getKey().equals(e.getVehicleId())))
                    .mapToDouble(e -> e.getAmount() != null ? e.getAmount().doubleValue() : 0.0).sum());
            double dist = round2(entry.getValue().stream().mapToDouble(t -> t.getDistanceKm() != null ? t.getDistanceKm() : 0.0).sum());
            byVehicle.add(new GroupSummary(entry.getKey(), name, entry.getValue().size(), rev, exp, round2(rev - exp), dist));
        }

        // Group by Driver
        Map<Long, Driver> driverMap = driverRepository.findAll().stream().collect(Collectors.toMap(Driver::getId, d -> d, (a, b) -> a));
        List<GroupSummary> byDriver = new ArrayList<>();
        Map<Long, List<Trip>> tripsByDr = filteredTrips.stream().filter(t -> t.getDriverId() != null).collect(Collectors.groupingBy(Trip::getDriverId));
        for (Map.Entry<Long, List<Trip>> entry : tripsByDr.entrySet()) {
            Driver d = driverMap.get(entry.getKey());
            String name = d != null ? d.getName() : ("Driver #" + entry.getKey());
            double rev = round2(entry.getValue().stream().mapToDouble(ReportService::tripRevenue).sum());
            Set<Long> dTripIds = entry.getValue().stream().map(Trip::getId).collect(Collectors.toSet());
            double exp = round2(filteredExpenses.stream()
                    .filter(e -> e.getTripId() != null && dTripIds.contains(e.getTripId()))
                    .mapToDouble(e -> e.getAmount() != null ? e.getAmount().doubleValue() : 0.0).sum());
            double dist = round2(entry.getValue().stream().mapToDouble(t -> t.getDistanceKm() != null ? t.getDistanceKm() : 0.0).sum());
            byDriver.add(new GroupSummary(entry.getKey(), name, entry.getValue().size(), rev, exp, round2(rev - exp), dist));
        }

        // Group by Customer
        Map<Long, Customer> customerMap = customerRepository.findAll().stream().collect(Collectors.toMap(Customer::getId, c -> c, (a, b) -> a));
        List<GroupSummary> byCustomer = new ArrayList<>();
        Map<Long, List<Trip>> tripsByCus = filteredTrips.stream().collect(Collectors.groupingBy(Trip::getCustomerId));
        for (Map.Entry<Long, List<Trip>> entry : tripsByCus.entrySet()) {
            Customer c = customerMap.get(entry.getKey());
            String name = c != null ? c.getName() : ("Customer #" + entry.getKey());
            double rev = round2(entry.getValue().stream().mapToDouble(ReportService::tripRevenue).sum());
            Set<Long> cTripIds = entry.getValue().stream().map(Trip::getId).collect(Collectors.toSet());
            double exp = round2(filteredExpenses.stream()
                    .filter(e -> e.getTripId() != null && cTripIds.contains(e.getTripId()))
                    .mapToDouble(e -> e.getAmount() != null ? e.getAmount().doubleValue() : 0.0).sum());
            double dist = round2(entry.getValue().stream().mapToDouble(t -> t.getDistanceKm() != null ? t.getDistanceKm() : 0.0).sum());
            byCustomer.add(new GroupSummary(entry.getKey(), name, entry.getValue().size(), rev, exp, round2(rev - exp), dist));
        }

        return new ReportSummaryResponse(
                filteredTrips.size(),
                totalRevenue,
                totalExpenses,
                totalProfit,
                totalDistance,
                filteredTrips,
                filteredExpenses,
                byVehicle,
                byDriver,
                byCustomer
        );
    }
}
