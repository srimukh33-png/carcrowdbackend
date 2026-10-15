package com.carcrowd.dto.customer;

public class OtpSentResponse {
    private String phoneNumber;
    private String message;
    private int expiresInSeconds;
    private String devOtp; // Only populated for local dev/testing

    public OtpSentResponse() {}

    public OtpSentResponse(String phoneNumber, String message, int expiresInSeconds, String devOtp) {
        this.phoneNumber = phoneNumber;
        this.message = message;
        this.expiresInSeconds = expiresInSeconds;
        this.devOtp = devOtp;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public void setExpiresInSeconds(int expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }

    public String getDevOtp() {
        return devOtp;
    }

    public void setDevOtp(String devOtp) {
        this.devOtp = devOtp;
    }
}
