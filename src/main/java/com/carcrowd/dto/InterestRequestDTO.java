package com.carcrowd.dto;

import jakarta.validation.constraints.*;

public class InterestRequestDTO {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 150, message = "Full name must be between 2 and 150 characters")
    @Pattern(regexp = "^[a-zA-Z\\s.'\\-]+$", message = "Full name must contain letters and standard name punctuation only")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^(\\+91[\\-\\s]?|0)?[0-9]{5}[\\-\\s]?[0-9]{5}$",
        message = "Phone number must be a valid 10-digit mobile number, optionally prefixed with +91"
    )
    private String phoneNumber;

    @NotBlank(message = "Email address is required")
    @Email(message = "Please enter a valid email address")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "You must be at least 18 years old to join a vehicle buying group")
    @Max(value = 120, message = "Please enter a valid age")
    private Integer age;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City name must not exceed 100 characters")
    private String city;

    @Size(max = 50, message = "Purchase timeline must not exceed 50 characters")
    private String purchaseTimeline;

    @AssertTrue(message = "Consent to the privacy policy and group negotiation processing is required")
    private boolean consent;

    @Size(max = 50, message = "Terms version must not exceed 50 characters")
    private String termsVersion;

    public InterestRequestDTO() {}

    public InterestRequestDTO(String fullName, String phoneNumber, String email, Integer age,
                              String city, String purchaseTimeline, boolean consent) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.age = age;
        this.city = city;
        this.purchaseTimeline = purchaseTimeline;
        this.consent = consent;
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

    public String getTermsVersion() {
        return termsVersion;
    }

    public void setTermsVersion(String termsVersion) {
        this.termsVersion = termsVersion;
    }
}
