package com.carcrowd.dto.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UpdateFollowUpRequest {

    private LocalDate nextFollowUpDate;
    private LocalDateTime lastContactedAt;
    private String internalNotes;

    public UpdateFollowUpRequest() {}

    public UpdateFollowUpRequest(LocalDate nextFollowUpDate, LocalDateTime lastContactedAt, String internalNotes) {
        this.nextFollowUpDate = nextFollowUpDate;
        this.lastContactedAt = lastContactedAt;
        this.internalNotes = internalNotes;
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
}
