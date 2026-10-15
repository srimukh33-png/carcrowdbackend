package com.carcrowd.dto.customer.request;

import com.carcrowd.entity.LeadStatus;
import java.time.LocalDateTime;

public class CustomerRequestSummaryDTO {

    private Long id;
    private Long carId;
    private Long brandId;
    private String brandName;
    private String modelName;
    private String imageUrl;
    private String city;
    private LocalDateTime joinedDate;
    private LocalDateTime lastUpdatedDate;
    private LeadStatus status;
    private String statusLabel;
    private String statusMessage;
    private String nextStep;

    public CustomerRequestSummaryDTO() {}

    public CustomerRequestSummaryDTO(Long id, Long carId, Long brandId, String brandName,
                                     String modelName, String imageUrl, String city,
                                     LocalDateTime joinedDate, LocalDateTime lastUpdatedDate,
                                     LeadStatus status, String statusLabel, String statusMessage,
                                     String nextStep) {
        this.id = id;
        this.carId = carId;
        this.brandId = brandId;
        this.brandName = brandName;
        this.modelName = modelName;
        this.imageUrl = imageUrl;
        this.city = city;
        this.joinedDate = joinedDate;
        this.lastUpdatedDate = lastUpdatedDate;
        this.status = status;
        this.statusLabel = statusLabel;
        this.statusMessage = statusMessage;
        this.nextStep = nextStep;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDateTime getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(LocalDateTime joinedDate) {
        this.joinedDate = joinedDate;
    }

    public LocalDateTime getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(LocalDateTime lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public LeadStatus getStatus() {
        return status;
    }

    public void setStatus(LeadStatus status) {
        this.status = status;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getNextStep() {
        return nextStep;
    }

    public void setNextStep(String nextStep) {
        this.nextStep = nextStep;
    }
}
