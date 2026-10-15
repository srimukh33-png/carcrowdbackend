package com.carcrowd.dto.admin;

import jakarta.validation.constraints.NotBlank;

public class CreateActivityRequest {

    @NotBlank(message = "Activity type is required")
    private String activityType;

    private String description;

    public CreateActivityRequest() {}

    public CreateActivityRequest(String activityType, String description) {
        this.activityType = activityType;
        this.description = description;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
