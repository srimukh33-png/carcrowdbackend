package com.carcrowd.dto.admin;

import java.time.LocalDateTime;

public class CarAdminDTO {

    private Long id;
    private Long brandId;
    private String brandName;
    private String modelName;
    private String imageUrl;
    private String bodyType;
    private String description;
    private String fuelType;
    private String transmission;
    private String seating;
    private boolean active;
    private LocalDateTime createdAt;
    private long buyerCount;
    private long totalRegistrationsCount;

    public CarAdminDTO() {}

    public CarAdminDTO(Long id, Long brandId, String brandName, String modelName, String imageUrl,
                       String bodyType, String description, String fuelType, String transmission,
                       String seating, boolean active, LocalDateTime createdAt,
                       long buyerCount, long totalRegistrationsCount) {
        this.id = id;
        this.brandId = brandId;
        this.brandName = brandName;
        this.modelName = modelName;
        this.imageUrl = imageUrl;
        this.bodyType = bodyType;
        this.description = description;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.seating = seating;
        this.active = active;
        this.createdAt = createdAt;
        this.buyerCount = buyerCount;
        this.totalRegistrationsCount = totalRegistrationsCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
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

    public long getBuyerCount() {
        return buyerCount;
    }

    public void setBuyerCount(long buyerCount) {
        this.buyerCount = buyerCount;
    }

    public long getTotalRegistrationsCount() {
        return totalRegistrationsCount;
    }

    public void setTotalRegistrationsCount(long totalRegistrationsCount) {
        this.totalRegistrationsCount = totalRegistrationsCount;
    }
}
