package com.transport.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.transport.dto.AdvanceRequest;
import com.transport.dto.DriverRequest;
import com.transport.dto.DriverResponse;
import com.transport.service.DriverService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

	private final DriverService service;

    public DriverController(DriverService service) {
        this.service = service;
    }

    @GetMapping
    public List<DriverResponse> list() {
        return service.list();
    }

    @PostMapping
    public DriverResponse create(@Valid @RequestBody DriverRequest req) {
        return service.save(req, null);
    }

    @PutMapping("/{id}")
    public DriverResponse update(@PathVariable Long id, @Valid @RequestBody DriverRequest req) {
        return service.save(req, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/{id}/advances")
    public DriverResponse addAdvance(@PathVariable Long id, @Valid @RequestBody AdvanceRequest req) {
        return service.addAdvance(id, req);
    }

    @DeleteMapping("/{id}/advances/{advanceId}")
    public DriverResponse deleteAdvance(@PathVariable Long id, @PathVariable Long advanceId) {
        return service.deleteAdvance(id, advanceId);
    }
}
