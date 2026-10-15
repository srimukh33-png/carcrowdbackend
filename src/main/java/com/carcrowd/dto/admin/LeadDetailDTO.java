package com.carcrowd.dto.admin;

import com.carcrowd.entity.LeadStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LeadDetailDTO {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private Integer age;
    private String city;
    private String brandName;
    private String modelName;
    private Long carId;
    private String purchaseTimeline;
    private boolean consent;
    private LeadStatus leadStatus;
    private LocalDateTime createdAt;
    private LocalDate nextFollowUpDate;
    private LocalDateTime lastContactedAt;
    private String internalNotes;
    private Long assignedMemberId;
    private String assignedMemberName;
    private String assignedMemberEmail;
    private String assignedMemberPhone;
    private LocalDateTime assignedAt;
    private String assignedBy;
    private List<LeadActivityDTO> activities = new ArrayList<>();

    public LeadDetailDTO() {}

    public LeadDetailDTO(Long id, String fullName, String phoneNumber, String email, Integer age,
                         String city, String brandName, String modelName, Long carId,
                         String purchaseTimeline, boolean consent, LeadStatus leadStatus,
                         LocalDateTime createdAt, LocalDate nextFollowUpDate,
                         LocalDateTime lastContactedAt, String internalNotes,
                         List<LeadActivityDTO> activities) {
        this(id, fullName, phoneNumber, email, age, city, brandName, modelName, carId, purchaseTimeline,
             consent, leadStatus, createdAt, nextFollowUpDate, lastContactedAt, internalNotes,
             null, null, null, null, null, null, activities);
    }

    public LeadDetailDTO(Long id, String fullName, String phoneNumber, String email, Integer age,
                         String city, String brandName, String modelName, Long carId,
                         String purchaseTimeline, boolean consent, LeadStatus leadStatus,
                         LocalDateTime createdAt, LocalDate nextFollowUpDate,
                         LocalDateTime lastContactedAt, String internalNotes,
                         Long assignedMemberId, String assignedMemberName, String assignedMemberEmail,
                         String assignedMemberPhone, LocalDateTime assignedAt, String assignedBy,
                         List<LeadActivityDTO> activities) {
        this.id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.age = age;
        this.city = city;
        this.brandName = brandName;
        this.modelName = modelName;
        this.carId = carId;
        this.purchaseTimeline = purchaseTimeline;
        this.consent = consent;
        this.leadStatus = leadStatus;
        this.createdAt = createdAt;
        this.nextFollowUpDate = nextFollowUpDate;
        this.lastContactedAt = lastContactedAt;
        this.internalNotes = internalNotes;
        this.assignedMemberId = assignedMemberId;
        this.assignedMemberName = assignedMemberName;
        this.assignedMemberEmail = assignedMemberEmail;
        this.assignedMemberPhone = assignedMemberPhone;
        this.assignedAt = assignedAt;
        this.assignedBy = assignedBy;
        this.activities = activities != null ? activities : new ArrayList<>();
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getPurchaseTimeline() {
        return purchaseTimeline;
    }

    public void setPurchaseTimeline(String purchaseTimeline) {
        this.purchaseTimeline = purchaseTimeline;
    }

    public boolean isConsent() {
        return consent;
    }

    public void setConsent(boolean consent) {
        this.consent = consent;
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

    public String getInternalNotes() {
        return internalNotes;
    }

    public void setInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
    }

    public List<LeadActivityDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<LeadActivityDTO> activities) {
        this.activities = activities;
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

    public String getAssignedMemberPhone() {
        return assignedMemberPhone;
    }

    public void setAssignedMemberPhone(String assignedMemberPhone) {
        this.assignedMemberPhone = assignedMemberPhone;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }
}
