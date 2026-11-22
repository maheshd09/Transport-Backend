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

import com.transport.dto.VehicleRequest;
import com.transport.entity.Vehicle;
import com.transport.service.VehicleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

	private final VehicleService service;

    public VehicleController(VehicleService service) {
        this.service = service;
    }

    @GetMapping
    public List<Vehicle> list() {
        return service.list();
    }

    @PostMapping
    public Vehicle create(@Valid @RequestBody VehicleRequest req) {
        return service.save(req, null);
    }

    @PutMapping("/{id}")
    public Vehicle update(@PathVariable Long id, @Valid @RequestBody VehicleRequest req) {
        return service.save(req, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
