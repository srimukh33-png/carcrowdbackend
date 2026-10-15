package com.carcrowd.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "interest_registrations",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_car_phone", columnNames = {"car_id", "phone_number"}),
        @UniqueConstraint(name = "uk_car_email", columnNames = {"car_id", "email"})
    },
    indexes = {
        @Index(name = "idx_reg_car_id", columnList = "car_id"),
        @Index(name = "idx_reg_user_id", columnList = "user_id"),
        @Index(name = "idx_reg_status", columnList = "lead_status"),
        @Index(name = "idx_reg_created_at", columnList = "created_at"),
        @Index(name = "idx_reg_assigned_member", columnList = "assigned_member_id")
    }
)
public class InterestRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 150)
    private String fullName;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(length = 50)
    private String purchaseTimeline;

    @Column(nullable = false)
    private boolean consent = false;

    @Column(name = "terms_accepted_at")
    private LocalDateTime termsAcceptedAt;

    @Column(name = "terms_version", length = 50)
    private String termsVersion = "2026-10-14";

    @Enumerated(EnumType.STRING)
    @Column(name = "lead_status", nullable = false, length = 30)
    private LeadStatus leadStatus = LeadStatus.JOINED;

    @Column(name = "last_contacted_at")
    private LocalDateTime lastContactedAt;

    @Column(name = "next_follow_up_date")
    private LocalDate nextFollowUpDate;

    @Column(name = "internal_notes", columnDefinition = "TEXT")
    private String internalNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_member_id")
    private AdminUser assignedMember;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "assigned_by", length = 150)
    private String assignedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public InterestRegistration() {}

    public InterestRegistration(Car car, String fullName, String phoneNumber, String email,
                                Integer age, String city, String purchaseTimeline, boolean consent) {
        this.car = car;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.age = age;
        this.city = city;
        this.purchaseTimeline = purchaseTimeline;
        this.consent = consent;
        this.leadStatus = LeadStatus.JOINED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public LocalDateTime getTermsAcceptedAt() {
        return termsAcceptedAt;
    }

    public void setTermsAcceptedAt(LocalDateTime termsAcceptedAt) {
        this.termsAcceptedAt = termsAcceptedAt;
    }

    public String getTermsVersion() {
        return termsVersion;
    }

    public void setTermsVersion(String termsVersion) {
        this.termsVersion = termsVersion;
    }

    public LeadStatus getLeadStatus() {
        return leadStatus;
    }

    public void setLeadStatus(LeadStatus leadStatus) {
        this.leadStatus = leadStatus;
    }

    public LocalDateTime getLastContactedAt() {
        return lastContactedAt;
    }

    public void setLastContactedAt(LocalDateTime lastContactedAt) {
        this.lastContactedAt = lastContactedAt;
    }

    public LocalDate getNextFollowUpDate() {
        return nextFollowUpDate;
    }

    public void setNextFollowUpDate(LocalDate nextFollowUpDate) {
        this.nextFollowUpDate = nextFollowUpDate;
    }

    public String getInternalNotes() {
        return internalNotes;
    }

    public void setInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
    }

    public AdminUser getAssignedMember() {
        return assignedMember;
    }

    public void setAssignedMember(AdminUser assignedMember) {
        this.assignedMember = assignedMember;
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
