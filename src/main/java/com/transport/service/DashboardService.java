package com.transport.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.transport.dto.DashboardStatsResponse;
import com.transport.dto.DashboardStatsResponse.MonthlyChartPoint;
import com.transport.entity.Driver;
import com.transport.entity.Expense;
import com.transport.entity.Trip;
import com.transport.entity.Vehicle;
import com.transport.repository.DriverRepository;
import com.transport.repository.ExpenseRepository;
import com.transport.repository.TripRepository;
import com.transport.repository.VehicleRepository;

@Service
public class DashboardService {

    private final TripRepository tripRepository;
    private final ExpenseRepository expenseRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    public DashboardService(TripRepository tripRepository,
                            ExpenseRepository expenseRepository,
                            VehicleRepository vehicleRepository,
                            DriverRepository driverRepository) {
        this.tripRepository = tripRepository;
        this.expenseRepository = expenseRepository;
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
    }

    private static double round2(double val) {
        return BigDecimal.valueOf(val).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @Transactional(readOnly = true)
    public DashboardStatsResponse getStats() {
        List<Trip> allTrips = tripRepository.findAllByOrderByCreatedAtDesc();
        List<Expense> allExpenses = expenseRepository.findAll();
        List<Vehicle> allVehicles = vehicleRepository.findAll();
        List<Driver> allDrivers = driverRepository.findAll();

        long totalTrips = allTrips.size();

        double totalRevenue = 0.0;
        for (Trip t : allTrips) {
            if (!"Cancelled".equalsIgnoreCase(t.getStatus())) {
                double dist = t.getDistanceKm() != null ? t.getDistanceKm() : 0.0;
                double rate = t.getRatePerKm() != null ? t.getRatePerKm().doubleValue() : 0.0;
                totalRevenue += (dist * rate);
            }
        }
        totalRevenue = round2(totalRevenue);

        double totalExpenses = 0.0;
        for (Expense e : allExpenses) {
            if (e.getAmount() != null) {
                totalExpenses += e.getAmount().doubleValue();
            }
        }
        totalExpenses = round2(totalExpenses);

        double totalProfit = round2(totalRevenue - totalExpenses);

        Set<Long> busyVehicleIds = allTrips.stream()
                .filter(t -> "Assigned".equals(t.getStatus()) || "In Transit".equals(t.getStatus()))
                .map(Trip::getVehicleId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        Set<Long> busyDriverIds = allTrips.stream()
                .filter(t -> "Assigned".equals(t.getStatus()) || "In Transit".equals(t.getStatus()))
                .map(Trip::getDriverId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        long availableVehicles = allVehicles.stream()
                .filter(v -> !v.isUnderMaintenance() && !busyVehicleIds.contains(v.getId()))
                .count();

        long availableDrivers = allDrivers.stream()
                .filter(d -> !d.isOnLeave() && !busyDriverIds.contains(d.getId()))
                .count();

        long activeTrips = allTrips.stream()
                .filter(t -> "Assigned".equals(t.getStatus()) || "In Transit".equals(t.getStatus()))
                .count();

        long completedTrips = allTrips.stream()
                .filter(t -> "Completed".equals(t.getStatus()) || "Delivered".equals(t.getStatus()))
                .count();

        // Calculate last 6 months chart
        LocalDate now = LocalDate.now();
        List<String> monthKeys = new ArrayList<>();
        DateTimeFormatter keyFmt = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter labelFmt = DateTimeFormatter.ofPattern("MMM yy", Locale.ENGLISH);

        Map<String, String> labels = new HashMap<>();
        Map<String, Double> monthRevenue = new HashMap<>();
        Map<String, Double> monthExpenses = new HashMap<>();

        for (int i = 5; i >= 0; i--) {
            LocalDate m = now.minusMonths(i);
            String k = m.format(keyFmt);
            monthKeys.add(k);
            labels.put(k, m.format(labelFmt));
            monthRevenue.put(k, 0.0);
            monthExpenses.put(k, 0.0);
        }

        for (Trip t : allTrips) {
            if (!"Cancelled".equalsIgnoreCase(t.getStatus()) && t.getPickupAt() != null) {
                String k = t.getPickupAt().format(keyFmt);
                if (monthRevenue.containsKey(k)) {
                    double dist = t.getDistanceKm() != null ? t.getDistanceKm() : 0.0;
                    double rate = t.getRatePerKm() != null ? t.getRatePerKm().doubleValue() : 0.0;
                    monthRevenue.put(k, monthRevenue.get(k) + (dist * rate));
                }
            }
        }

        for (Expense e : allExpenses) {
            if (e.getDate() != null && e.getAmount() != null) {
                String k = e.getDate().format(keyFmt);
                if (monthExpenses.containsKey(k)) {
                    monthExpenses.put(k, monthExpenses.get(k) + e.getAmount().doubleValue());
                }
            }
        }

        List<MonthlyChartPoint> chart = new ArrayList<>();
        for (String k : monthKeys) {
            double r = round2(monthRevenue.getOrDefault(k, 0.0));
            double exp = round2(monthExpenses.getOrDefault(k, 0.0));
            double p = round2(r - exp);
            chart.add(new MonthlyChartPoint(k, labels.get(k), r, exp, p));
        }

        List<Trip> recent = allTrips.stream().limit(6).toList();

        return new DashboardStatsResponse(
                totalTrips,
                totalRevenue,
                totalExpenses,
                totalProfit,
                availableVehicles,
                allVehicles.size(),
                availableDrivers,
                allDrivers.size(),
                activeTrips,
                completedTrips,
                chart,
                recent
        );
    }
}
