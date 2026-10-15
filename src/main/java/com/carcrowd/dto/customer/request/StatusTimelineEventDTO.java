package com.carcrowd.dto.customer.request;

import java.time.LocalDateTime;

public class StatusTimelineEventDTO {

    private String status;
    private String displayLabel;
    private LocalDateTime timestamp;
    private String description;
    private boolean completed;
    private boolean current;

    public StatusTimelineEventDTO() {}

    public StatusTimelineEventDTO(String status, String displayLabel, LocalDateTime timestamp,
                                  String description, boolean completed, boolean current) {
        this.status = status;
        this.displayLabel = displayLabel;
        this.timestamp = timestamp;
        this.description = description;
        this.completed = completed;
        this.current = current;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public void setDisplayLabel(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }
}
