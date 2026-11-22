package com.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.transport.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long>{

	
}
