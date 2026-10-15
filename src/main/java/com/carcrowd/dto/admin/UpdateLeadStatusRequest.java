package com.carcrowd.dto.admin;

import com.carcrowd.entity.LeadStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateLeadStatusRequest {

    @NotNull(message = "Lead status is required")
    private LeadStatus status;

    private String note;

    public UpdateLeadStatusRequest() {}

    public UpdateLeadStatusRequest(LeadStatus status, String note) {
        this.status = status;
        this.note = note;
    }

    public LeadStatus getStatus() {
        return status;
    }

    public void setStatus(LeadStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
