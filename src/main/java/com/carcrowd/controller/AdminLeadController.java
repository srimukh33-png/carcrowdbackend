package com.carcrowd.controller;

import com.carcrowd.dto.admin.*;
import com.carcrowd.entity.LeadStatus;
import com.carcrowd.service.AdminLeadService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/leads")
public class AdminLeadController {

    private final AdminLeadService leadService;

    public AdminLeadController(AdminLeadService leadService) {
        this.leadService = leadService;
    }

    @GetMapping
    public ResponseEntity<Page<LeadSummaryDTO>> getLeads(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String purchaseTimeline,
            @RequestParam(required = false) LeadStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long assignedMemberId,
            @RequestParam(required = false) Boolean unassigned,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Page<LeadSummaryDTO> leads = leadService.getLeads(
            search, brand, model, city, purchaseTimeline, status, startDate, endDate, assignedMemberId, unassigned, page, size, sortBy, sortDirection
        );
        return ResponseEntity.ok(leads);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeadDetailDTO> getLeadById(@PathVariable Long id) {
        return ResponseEntity.ok(leadService.getLeadById(id));
    }

    private String requireAdminEmail(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new com.carcrowd.exception.UnauthorizedException("Full administrative authentication required");
        }
        return authentication.getName();
    }

    @RequestMapping(value = "/{id}/status", method = {RequestMethod.PATCH, RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<LeadDetailDTO> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLeadStatusRequest request,
            Authentication authentication
    ) {
        String adminEmail = requireAdminEmail(authentication);
        return ResponseEntity.ok(leadService.updateLeadStatus(id, request, adminEmail, adminEmail));
    }

    @RequestMapping(value = "/{id}/follow-up", method = {RequestMethod.PATCH, RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<LeadDetailDTO> updateFollowUp(
            @PathVariable Long id,
            @Valid @RequestBody UpdateFollowUpRequest request,
            Authentication authentication
    ) {
        String adminEmail = requireAdminEmail(authentication);
        return ResponseEntity.ok(leadService.updateFollowUp(id, request, adminEmail, adminEmail));
    }

    @PostMapping("/{id}/activities")
    public ResponseEntity<LeadActivityDTO> addActivity(
            @PathVariable Long id,
            @Valid @RequestBody CreateActivityRequest request,
            Authentication authentication
    ) {
        String adminEmail = requireAdminEmail(authentication);
        return ResponseEntity.ok(leadService.addActivity(id, request, adminEmail, adminEmail));
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<LeadActivityDTO>> getActivities(@PathVariable Long id) {
        return ResponseEntity.ok(leadService.getActivities(id));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportLeads(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String purchaseTimeline,
            @RequestParam(required = false) LeadStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            Authentication authentication
    ) {
        String adminEmail = requireAdminEmail(authentication);
        String csvData = leadService.exportLeadsToCsv(
            search, brand, model, city, purchaseTimeline, status, startDate, endDate, sortBy, sortDirection, adminEmail
        );

        byte[] csvBytes = csvData.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"carcrowd-leads-" + LocalDate.now() + ".csv\"")
            .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
            .body(csvBytes);
    }
}
