package com.carcrowd.dto.admin;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class BulkAssignPayload {

    @NotEmpty(message = "At least one request ID must be provided")
    private List<Long> requestIds;

    private Long memberId;

    public BulkAssignPayload() {}

    public BulkAssignPayload(List<Long> requestIds, Long memberId) {
        this.requestIds = requestIds;
        this.memberId = memberId;
    }

    public List<Long> getRequestIds() {
        return requestIds;
    }

    public void setRequestIds(List<Long> requestIds) {
        this.requestIds = requestIds;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
}
