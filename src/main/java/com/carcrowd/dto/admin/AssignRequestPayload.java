package com.carcrowd.dto.admin;

public class AssignRequestPayload {

    private Long memberId;

    public AssignRequestPayload() {}

    public AssignRequestPayload(Long memberId) {
        this.memberId = memberId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
}
