package com.carcrowd.entity;

public enum LeadStatus {
    JOINED("Request Submitted", "Your request has been submitted successfully. Our CarCrowd team will contact you to confirm your interest.", "Wait for the CarCrowd team to call."),
    CONTACTED("Team Contacted", "Our team has contacted you. We are updating your request based on your interest.", "The team records the customer's decision."),
    WAITING("Waiting for the Next Step", "Great! Your interest is confirmed. You are now waiting for the next step from CarCrowd. We will contact you when there is an update.", "Wait for CarCrowd to coordinate dealership negotiations after confirming interest."),
    BOUGHT("Car Bought", "Congratulations! You have purchased your car through CarCrowd group buying. Enjoy your new ride!", "Deal completed successfully."),
    CANCELLED("Request Cancelled", "Your request has been cancelled as you are not interested in proceeding at this time.", "No further action unless you express interest again.");

    private final String displayLabel;
    private final String customerMessage;
    private final String nextStep;

    LeadStatus(String displayLabel, String customerMessage, String nextStep) {
        this.displayLabel = displayLabel;
        this.customerMessage = customerMessage;
        this.nextStep = nextStep;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public String getCustomerMessage() {
        return customerMessage;
    }

    public String getNextStep() {
        return nextStep;
    }
}
