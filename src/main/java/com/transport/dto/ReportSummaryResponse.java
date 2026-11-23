package com.transport.dto;

import java.util.List;

import com.transport.entity.Expense;
import com.transport.entity.Trip;

public record ReportSummaryResponse(
    long totalTrips,
    double totalRevenue,
    double totalExpenses,
    double totalProfit,
    double totalDistanceKm,
    List<Trip> trips,
    List<Expense> expenses,
    List<GroupSummary> byVehicle,
    List<GroupSummary> byDriver,
    List<GroupSummary> byCustomer
) {
    public record GroupSummary(
        Long id,
        String name,
        long trips,
        double revenue,
        double expenses,
        double profit,
        double distance
    ) {}
}
