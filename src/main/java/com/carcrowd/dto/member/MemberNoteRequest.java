package com.carcrowd.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MemberNoteRequest {

    @NotBlank(message = "Note content is required")
    @Size(max = 2000, message = "Note cannot exceed 2000 characters")
    private String note;

    public MemberNoteRequest() {}

    public MemberNoteRequest(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
