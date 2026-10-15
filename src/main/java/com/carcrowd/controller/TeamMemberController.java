package com.carcrowd.controller;

import com.carcrowd.dto.admin.*;
import com.carcrowd.service.TeamMemberService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    public TeamMemberController(TeamMemberService teamMemberService) {
        this.teamMemberService = teamMemberService;
    }

    @GetMapping("/members")
    public ResponseEntity<Page<TeamMemberDTO>> getMembers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        return ResponseEntity.ok(teamMemberService.getMembers(search, active, page, size, sortBy, sortDirection));
    }

    @GetMapping("/members/active")
    public ResponseEntity<List<TeamMemberDTO>> getActiveMembers() {
        return ResponseEntity.ok(teamMemberService.getActiveMembersList());
    }

    @GetMapping("/members/{id}")
    public ResponseEntity<TeamMemberDTO> getMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(teamMemberService.getMemberById(id));
    }

    private String requireAdminEmail(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new com.carcrowd.exception.UnauthorizedException("Full administrative authentication required");
        }
        return authentication.getName();
    }

    @PostMapping("/members")
    public ResponseEntity<TeamMemberDTO> createMember(
            @Valid @RequestBody CreateMemberRequest request,
            Authentication authentication
    ) {
        String adminEmail = requireAdminEmail(authentication);
        TeamMemberDTO created = teamMemberService.createMember(request, adminEmail);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/members/{id}")
    public ResponseEntity<TeamMemberDTO> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMemberRequest request,
            Authentication authentication
    ) {
        String adminEmail = requireAdminEmail(authentication);
        return ResponseEntity.ok(teamMemberService.updateMember(id, request, adminEmail));
    }

    @PatchMapping("/members/{id}/status")
    public ResponseEntity<TeamMemberDTO> updateMemberStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMemberStatusRequest request,
            Authentication authentication
    ) {
        String adminEmail = requireAdminEmail(authentication);
        String adminName = adminEmail;
        return ResponseEntity.ok(teamMemberService.updateMemberStatus(id, request, adminEmail, adminName));
    }

    @DeleteMapping("/members/{id}")
    public ResponseEntity<Map<String, String>> deleteMember(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String adminEmail = requireAdminEmail(authentication);
        teamMemberService.deleteMember(id, adminEmail);
        return ResponseEntity.ok(Map.of("message", "Team member successfully deleted"));
    }

    @GetMapping("/members/{id}/customers")
    public ResponseEntity<Page<LeadSummaryDTO>> getMemberCustomers(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(teamMemberService.getMemberCustomers(id, page, size));
    }

    @PatchMapping(value = {"/requests/{requestId}/assign", "/leads/{requestId}/assign"})
    public ResponseEntity<LeadDetailDTO> assignRequest(
            @PathVariable Long requestId,
            @RequestBody AssignRequestPayload payload,
            Authentication authentication
    ) {
        String adminEmail = requireAdminEmail(authentication);
        String adminName = adminEmail;
        return ResponseEntity.ok(teamMemberService.assignRequest(requestId, payload.getMemberId(), adminEmail, adminName));
    }

    @PostMapping(value = {"/requests/bulk-assign", "/leads/bulk-assign"})
    public ResponseEntity<Map<String, Object>> bulkAssign(
            @Valid @RequestBody BulkAssignPayload payload,
            Authentication authentication
    ) {
        String adminEmail = requireAdminEmail(authentication);
        String adminName = adminEmail;
        int count = teamMemberService.bulkAssign(payload.getRequestIds(), payload.getMemberId(), adminEmail, adminName);
        return ResponseEntity.ok(Map.of("assignedCount", count, "message", "Successfully assigned " + count + " customer requests"));
    }

    @GetMapping(value = {"/requests/unassigned", "/leads/unassigned"})
    public ResponseEntity<Page<LeadSummaryDTO>> getUnassignedRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(teamMemberService.getUnassignedRequests(page, size));
    }
}
