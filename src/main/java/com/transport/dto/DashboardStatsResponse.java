package com.transport.dto;

import java.util.List;

import com.transport.entity.Trip;

public record DashboardStatsResponse(
    long totalTrips,
    double totalRevenue,
    double totalExpenses,
    double totalProfit,
    long availableVehicles,
    long totalVehicles,
    long availableDrivers,
    long totalDrivers,
    long activeTrips,
    long completedTrips,
    List<MonthlyChartPoint> monthlyChart,
    List<Trip> recentTrips
) {
    public record MonthlyChartPoint(
        String key,
        String month,
        double revenue,
        double expenses,
        double profit
    ) {}
}
