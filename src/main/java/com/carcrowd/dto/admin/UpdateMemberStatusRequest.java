package com.carcrowd.dto.admin;

import jakarta.validation.constraints.NotNull;

public class UpdateMemberStatusRequest {

    @NotNull(message = "Active status is required")
    private Boolean active;

    private Long reassignToMemberId;

    public UpdateMemberStatusRequest() {}

    public UpdateMemberStatusRequest(Boolean active) {
        this.active = active;
    }

    public UpdateMemberStatusRequest(Boolean active, Long reassignToMemberId) {
        this.active = active;
        this.reassignToMemberId = reassignToMemberId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Long getReassignToMemberId() {
        return reassignToMemberId;
    }

    public void setReassignToMemberId(Long reassignToMemberId) {
        this.reassignToMemberId = reassignToMemberId;
    }
}
