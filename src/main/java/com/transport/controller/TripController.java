package com.transport.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.transport.dto.PagedResponse;
import com.transport.dto.TripRequest;
import com.transport.entity.Trip;
import com.transport.service.TripService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService service;

    public TripController(TripService service) {
        this.service = service;
    }

    /**
     * GET /api/trips?page=0&size=20&status=Pending&vehicleId=5
     * Returns paginated response with metadata.
     */
    @GetMapping
    public PagedResponse<Trip> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String vehicleId,
            @RequestParam(required = false) String q) {
        // Cap page size to prevent abuse
        int safeSize = Math.min(size, 200);

        String cleanStatus = null;
        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status) && !"__all__".equalsIgnoreCase(status)) {
            cleanStatus = status.trim();
        }

        Long cleanVehicleId = null;
        if (vehicleId != null && !vehicleId.isBlank() && !"ALL".equalsIgnoreCase(vehicleId) && !"__all__".equalsIgnoreCase(vehicleId)) {
            try {
                cleanVehicleId = Long.parseLong(vehicleId.trim());
            } catch (NumberFormatException ignored) {
            }
        }

        String cleanQ = (q != null && !q.isBlank()) ? q.trim() : null;

        return service.list(page, safeSize, cleanStatus, cleanVehicleId, cleanQ);
    }

    @GetMapping("/{id}")
    public Trip get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public Trip create(@Valid @RequestBody TripRequest req) {
        return service.save(req, null);
    }

    @PutMapping("/{id}")
    public Trip update(@PathVariable Long id, @Valid @RequestBody TripRequest req) {
        return service.save(req, id);
    }

    @PatchMapping("/{id}/status")
    public Trip setStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return service.setStatus(id, body.get("status"));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
