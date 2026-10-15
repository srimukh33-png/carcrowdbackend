package com.carcrowd.dto.admin;

import java.time.LocalDateTime;

public class TeamMemberDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String role;
    private boolean active;
    private long assignedCustomerCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TeamMemberDTO() {}

    public TeamMemberDTO(Long id, String fullName, String email, String phoneNumber,
                         String role, boolean active, long assignedCustomerCount,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.active = active;
        this.assignedCustomerCount = assignedCustomerCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getAssignedCustomerCount() {
        return assignedCustomerCount;
    }

    public void setAssignedCustomerCount(long assignedCustomerCount) {
        this.assignedCustomerCount = assignedCustomerCount;
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
