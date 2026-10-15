package com.carcrowd.dto.admin;

import com.carcrowd.entity.LeadStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeadSummaryDTO {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String brandName;
    private String modelName;
    private Long carId;
    private String city;
    private Integer age;
    private String purchaseTimeline;
    private LeadStatus leadStatus;
    private LocalDateTime createdAt;
    private LocalDate nextFollowUpDate;
    private LocalDateTime lastContactedAt;
    private Long assignedMemberId;
    private String assignedMemberName;
    private String assignedMemberEmail;
    private LocalDateTime assignedAt;

    public LeadSummaryDTO() {}

    public LeadSummaryDTO(Long id, String fullName, String phoneNumber, String email, String brandName,
                          String modelName, Long carId, String city, Integer age, String purchaseTimeline,
                          LeadStatus leadStatus, LocalDateTime createdAt, LocalDate nextFollowUpDate,
                          LocalDateTime lastContactedAt) {
        this(id, fullName, phoneNumber, email, brandName, modelName, carId, city, age, purchaseTimeline,
             leadStatus, createdAt, nextFollowUpDate, lastContactedAt, null, null, null, null);
    }

    public LeadSummaryDTO(Long id, String fullName, String phoneNumber, String email, String brandName,
                          String modelName, Long carId, String city, Integer age, String purchaseTimeline,
                          LeadStatus leadStatus, LocalDateTime createdAt, LocalDate nextFollowUpDate,
                          LocalDateTime lastContactedAt, Long assignedMemberId, String assignedMemberName,
                          String assignedMemberEmail, LocalDateTime assignedAt) {
        this.id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.brandName = brandName;
        this.modelName = modelName;
        this.carId = carId;
        this.city = city;
        this.age = age;
        this.purchaseTimeline = purchaseTimeline;
        this.leadStatus = leadStatus;
        this.createdAt = createdAt;
        this.nextFollowUpDate = nextFollowUpDate;
        this.lastContactedAt = lastContactedAt;
        this.assignedMemberId = assignedMemberId;
        this.assignedMemberName = assignedMemberName;
        this.assignedMemberEmail = assignedMemberEmail;
        this.assignedAt = assignedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPurchaseTimeline() {
        return purchaseTimeline;
    }

    public void setPurchaseTimeline(String purchaseTimeline) {
        this.purchaseTimeline = purchaseTimeline;
    }

    public LeadStatus getLeadStatus() {
        return leadStatus;
    }

    public void setLeadStatus(LeadStatus leadStatus) {
        this.leadStatus = leadStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getNextFollowUpDate() {
        return nextFollowUpDate;
    }

    public void setNextFollowUpDate(LocalDate nextFollowUpDate) {
        this.nextFollowUpDate = nextFollowUpDate;
    }

    public LocalDateTime getLastContactedAt() {
        return lastContactedAt;
    }

    public void setLastContactedAt(LocalDateTime lastContactedAt) {
        this.lastContactedAt = lastContactedAt;
    }

    public Long getAssignedMemberId() {
        return assignedMemberId;
    }

    public void setAssignedMemberId(Long assignedMemberId) {
        this.assignedMemberId = assignedMemberId;
    }

    public String getAssignedMemberName() {
        return assignedMemberName;
    }

    public void setAssignedMemberName(String assignedMemberName) {
        this.assignedMemberName = assignedMemberName;
    }

    public String getAssignedMemberEmail() {
        return assignedMemberEmail;
    }

    public void setAssignedMemberEmail(String assignedMemberEmail) {
        this.assignedMemberEmail = assignedMemberEmail;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
}
