package com.carcrowd.controller;

import com.carcrowd.dto.admin.LeadDetailDTO;
import com.carcrowd.dto.admin.LeadSummaryDTO;
import com.carcrowd.dto.admin.TeamMemberDTO;
import com.carcrowd.dto.admin.UpdateLeadStatusRequest;
import com.carcrowd.dto.member.MemberNoteRequest;
import com.carcrowd.entity.LeadStatus;
import com.carcrowd.service.MemberLeadService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
public class MemberRequestController {

    private final MemberLeadService memberLeadService;

    public MemberRequestController(MemberLeadService memberLeadService) {
        this.memberLeadService = memberLeadService;
    }

    @GetMapping("/me")
    public ResponseEntity<TeamMemberDTO> getMyProfile(Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(memberLeadService.getMemberProfile(email));
    }

    @GetMapping("/requests")
    public ResponseEntity<Page<LeadSummaryDTO>> getMyRequests(
            @RequestParam(required = false) LeadStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
    ) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(memberLeadService.getAssignedRequests(email, status, page, size));
    }

    @GetMapping("/requests/{requestId}")
    public ResponseEntity<LeadDetailDTO> getMyRequestById(
            @PathVariable Long requestId,
            Authentication authentication
    ) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(memberLeadService.getAssignedRequestById(email, requestId));
    }

    @PatchMapping("/requests/{requestId}/status")
    public ResponseEntity<LeadDetailDTO> updateMyRequestStatus(
            @PathVariable Long requestId,
            @Valid @RequestBody UpdateLeadStatusRequest request,
            Authentication authentication
    ) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(memberLeadService.updateAssignedRequestStatus(email, requestId, request));
    }

    @PostMapping("/requests/{requestId}/notes")
    public ResponseEntity<LeadDetailDTO> addNoteToMyRequest(
            @PathVariable Long requestId,
            @Valid @RequestBody MemberNoteRequest request,
            Authentication authentication
    ) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(memberLeadService.addNote(email, requestId, request));
    }
}
