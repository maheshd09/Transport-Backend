package com.transport.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "vehicles", indexes = {
        @Index(columnList = "createdAt")
})
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
}
