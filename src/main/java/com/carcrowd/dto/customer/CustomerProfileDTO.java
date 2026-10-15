package com.carcrowd.dto.customer;

public class CustomerProfileDTO {
    private Long id;
    private String email;
    private String fullName;
    private String profilePictureUrl;
    private String phoneNumber;
    private boolean phoneVerified;
    private boolean profileComplete;

    public CustomerProfileDTO() {}

    public CustomerProfileDTO(Long id, String email, String fullName, String profilePictureUrl,
                              String phoneNumber, boolean phoneVerified, boolean profileComplete) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.profilePictureUrl = profilePictureUrl;
        this.phoneNumber = phoneNumber;
        this.phoneVerified = phoneVerified;
        this.profileComplete = profileComplete;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public boolean isProfileComplete() {
        return profileComplete;
    }

    public void setProfileComplete(boolean profileComplete) {
        this.profileComplete = profileComplete;
    }
}
