package com.carcrowd.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "brand_id", nullable = false)
    private CarBrand brand;

    @Column(nullable = false, length = 100)
    private String modelName;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(length = 50)
    private String bodyType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String fuelType;

    @Column(length = 100)
    private String transmission;

    @Column(length = 50)
    private String seating;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterestRegistration> registrations = new ArrayList<>();

    public Car() {}

    public Car(CarBrand brand, String modelName, String imageUrl, String bodyType, String description,
               String fuelType, String transmission, String seating, boolean active) {
        this.brand = brand;
        this.modelName = modelName;
        this.imageUrl = imageUrl;
        this.bodyType = bodyType;
        this.description = description;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.seating = seating;
        this.active = active;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CarBrand getBrand() {
        return brand;
    }

    public void setBrand(CarBrand brand) {
        this.brand = brand;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getSeating() {
        return seating;
    }

    public void setSeating(String seating) {
        this.seating = seating;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<InterestRegistration> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(List<InterestRegistration> registrations) {
        this.registrations = registrations;
    }
}
