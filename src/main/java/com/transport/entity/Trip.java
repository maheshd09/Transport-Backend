package com.transport.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "trips", indexes = {
        @Index(columnList = "customerId"),
        @Index(columnList = "vehicleId"),
        @Index(columnList = "driverId"),
        @Index(columnList = "status"),
        @Index(columnList = "createdAt")  // For pagination ORDER BY
})
@Getter
@Setter
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code; // TRP-0012

    @Column(length = 20)
    private String vehicleType; // Truck / Tanker

    @Column(nullable = false)
    private Long customerId;

    @Column(length = 255)
    private String pickupLocation;

    @Column(length = 255)
    private String dropLocation;

    @Column(nullable = false)
    private LocalDateTime pickupAt;

    @Column(nullable = false)
    private LocalDateTime deliveryAt;

    @Column(length = 150)
    private String material;

    private Double quantity;

    @Column(length = 30)
    private String quantityUnit;

    private Double weightTons;

    private Long vehicleId;

    private Long driverId;

    @Column(nullable = false)
    private Double distanceKm = 0.0;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal ratePerKm = BigDecimal.ZERO;

    @Column(nullable = false, length = 20)
    private String status = "Pending";

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public String getPickupLocation() {
		return pickupLocation;
	}

	public void setPickupLocation(String pickupLocation) {
		this.pickupLocation = pickupLocation;
	}

	public String getDropLocation() {
		return dropLocation;
	}

	public void setDropLocation(String dropLocation) {
		this.dropLocation = dropLocation;
	}

	public LocalDateTime getPickupAt() {
		return pickupAt;
	}

	public void setPickupAt(LocalDateTime pickupAt) {
		this.pickupAt = pickupAt;
	}

	public LocalDateTime getDeliveryAt() {
		return deliveryAt;
	}

	public void setDeliveryAt(LocalDateTime deliveryAt) {
		this.deliveryAt = deliveryAt;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public String getQuantityUnit() {
		return quantityUnit;
	}

	public void setQuantityUnit(String quantityUnit) {
		this.quantityUnit = quantityUnit;
	}

	public Double getWeightTons() {
		return weightTons;
	}

	public void setWeightTons(Double weightTons) {
		this.weightTons = weightTons;
	}

	public Long getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(Long vehicleId) {
		this.vehicleId = vehicleId;
	}

	public Long getDriverId() {
		return driverId;
	}

	public void setDriverId(Long driverId) {
		this.driverId = driverId;
	}

	public Double getDistanceKm() {
		return distanceKm;
	}

	public void setDistanceKm(Double distanceKm) {
		this.distanceKm = distanceKm;
	}

	public BigDecimal getRatePerKm() {
		return ratePerKm;
	}

	public void setRatePerKm(BigDecimal ratePerKm) {
		this.ratePerKm = ratePerKm;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	
}
