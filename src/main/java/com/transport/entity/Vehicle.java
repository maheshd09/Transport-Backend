package com.transport.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "vehicles")
@Getter
@Setter
public class Vehicle {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String number;

    @Column(nullable = false, length = 30)
    private String type;

    @Column(length = 100)
    private String model;

    private Double capacityValue;

    @Column(length = 10)
    private String capacityUnit; // tons / kg / litres

    private Double capacityTons;

    private LocalDate insuranceExpiry;

    @Column(length = 50)
    private String registrationNo;

    private LocalDate registrationExpiry;

    @Column(nullable = false)
    private boolean underMaintenance = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Double getCapacityValue() {
		return capacityValue;
	}

	public void setCapacityValue(Double capacityValue) {
		this.capacityValue = capacityValue;
	}

	public String getCapacityUnit() {
		return capacityUnit;
	}

	public void setCapacityUnit(String capacityUnit) {
		this.capacityUnit = capacityUnit;
	}

	public Double getCapacityTons() {
		return capacityTons;
	}

	public void setCapacityTons(Double capacityTons) {
		this.capacityTons = capacityTons;
	}

	public LocalDate getInsuranceExpiry() {
		return insuranceExpiry;
	}

	public void setInsuranceExpiry(LocalDate insuranceExpiry) {
		this.insuranceExpiry = insuranceExpiry;
	}

	public String getRegistrationNo() {
		return registrationNo;
	}

	public void setRegistrationNo(String registrationNo) {
		this.registrationNo = registrationNo;
	}

	public LocalDate getRegistrationExpiry() {
		return registrationExpiry;
	}

	public void setRegistrationExpiry(LocalDate registrationExpiry) {
		this.registrationExpiry = registrationExpiry;
	}

	public boolean isUnderMaintenance() {
		return underMaintenance;
	}

	public void setUnderMaintenance(boolean underMaintenance) {
		this.underMaintenance = underMaintenance;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	    
}
