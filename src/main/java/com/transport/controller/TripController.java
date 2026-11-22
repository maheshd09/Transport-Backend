package com.transport.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping
    public List<Trip> list() {
        return service.list();
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
