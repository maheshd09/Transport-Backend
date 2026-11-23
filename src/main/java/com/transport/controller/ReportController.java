package com.transport.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.transport.dto.ReportSummaryResponse;
import com.transport.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/summary")
    public ReportSummaryResponse getSummary(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(required = false) Long vehicleId,
            @RequestParam(required = false) Long driverId,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String status) {
        return reportService.generateReport(start, end, vehicleId, driverId, customerId, status);
    }
}
