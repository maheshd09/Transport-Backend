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

import com.transport.dto.CustomerRequest;
import com.transport.entity.Customer;
import com.transport.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

	private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    public List<Customer> list() {
        return service.list();
    }

    @PostMapping
    public Customer create(@Valid @RequestBody CustomerRequest req) {
        return service.save(req, null);
    }

    @PutMapping("/{id}")
    public Customer update(@PathVariable Long id, @Valid @RequestBody CustomerRequest req) {
        return service.save(req, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
	
}
