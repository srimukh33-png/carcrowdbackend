package com.carcrowd.dto.admin;

import java.time.LocalDateTime;

public class LeadActivityDTO {
    private Long id;
    private Long interestRegistrationId;
    private String adminEmail;
    private String adminName;
    private String activityType;
    private String description;
    private LocalDateTime createdAt;

    public LeadActivityDTO() {}

    public LeadActivityDTO(Long id, Long interestRegistrationId, String adminEmail, String adminName,
                           String activityType, String description, LocalDateTime createdAt) {
        this.id = id;
        this.interestRegistrationId = interestRegistrationId;
        this.adminEmail = adminEmail;
        this.adminName = adminName;
        this.activityType = activityType;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInterestRegistrationId() {
        return interestRegistrationId;
    }

    public void setInterestRegistrationId(Long interestRegistrationId) {
        this.interestRegistrationId = interestRegistrationId;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
