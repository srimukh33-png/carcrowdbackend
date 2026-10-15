package com.carcrowd.dto.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UpdatePhoneRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^(\\+91[\\-\\s]?)?[0-9]{10}$",
        message = "Please enter a valid 10-digit mobile number"
    )
    private String phoneNumber;

    public UpdatePhoneRequest() {}

    public UpdatePhoneRequest(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
