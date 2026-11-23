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
}
