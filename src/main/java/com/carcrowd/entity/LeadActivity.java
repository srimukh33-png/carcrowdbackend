package com.carcrowd.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "lead_activities",
    indexes = {
        @Index(name = "idx_act_lead_id", columnList = "interest_registration_id"),
        @Index(name = "idx_act_created_at", columnList = "created_at")
    }
)
public class LeadActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "interest_registration_id", nullable = false)
    private Long interestRegistrationId;

    @Column(name = "admin_email", length = 150)
    private String adminEmail;

    @Column(name = "admin_name", length = 150)
    private String adminName;

    @Column(name = "activity_type", nullable = false, length = 50)
    private String activityType;

    @Column(name = "previous_status", length = 30)
    private String previousStatus;

    @Column(name = "new_status", length = 30)
    private String newStatus;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public LeadActivity() {}

    public LeadActivity(Long interestRegistrationId, String adminEmail, String adminName,
                        String activityType, String description) {
        this.interestRegistrationId = interestRegistrationId;
        this.adminEmail = adminEmail;
        this.adminName = adminName;
        this.activityType = activityType;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    public LeadActivity(Long interestRegistrationId, String adminEmail, String adminName,
                        String activityType, String previousStatus, String newStatus, String description) {
        this.interestRegistrationId = interestRegistrationId;
        this.adminEmail = adminEmail;
        this.adminName = adminName;
        this.activityType = activityType;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.description = description;
        this.createdAt = LocalDateTime.now();
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

    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
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
