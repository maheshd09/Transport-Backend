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

import com.transport.dto.ExpenseRequest;
import com.transport.entity.Expense;
import com.transport.service.ExpenseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

	
	private final ExpenseService service;

    public ExpenseController(ExpenseService service) {
        this.service = service;
    }

    @GetMapping
    public List<Expense> list() {
        return service.list();
    }

    @PostMapping
    public Expense create(@Valid @RequestBody ExpenseRequest req) {
        return service.save(req, null);
    }

    @PutMapping("/{id}")
    public Expense update(@PathVariable Long id, @Valid @RequestBody ExpenseRequest req) {
        return service.save(req, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
