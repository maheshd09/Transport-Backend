package com.transport.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.transport.dto.CustomerRequest;
import com.transport.entity.Customer;
import com.transport.exception.ApiException;
import com.transport.repository.CustomerRepository;
import com.transport.repository.TripRepository;

@Service
public class CustomerService {
	private final CustomerRepository customerRepository;
    private final TripRepository tripRepository;

    public CustomerService(CustomerRepository customerRepository, TripRepository tripRepository) {
        this.customerRepository = customerRepository;
        this.tripRepository = tripRepository;
    }

    public List<Customer> list() {
        return customerRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .toList();
    }

    public Customer save(CustomerRequest req, Long id) {
        Customer c;
        if (id != null) {
            c = customerRepository.findById(id)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Customer not found"));
        } else {
            c = new Customer();
        }
        c.setName(req.name());
        c.setPhone(req.phone());
        c.setAddress(req.address());
        c.setCompany(req.company());
        return customerRepository.save(c);
    }

    public void delete(Long id) {
        long count = tripRepository.countByCustomerId(id);
        if (count > 0) {
            throw new ApiException("This customer has " + count
                    + " trip(s). Delete or reassign those trips first.");
        }
        customerRepository.deleteById(id);
    }
}
