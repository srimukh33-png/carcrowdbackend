package com.carcrowd.controller;

import com.carcrowd.dto.customer.request.CustomerRequestDetailDTO;
import com.carcrowd.dto.customer.request.CustomerRequestSummaryDTO;
import com.carcrowd.service.CustomerRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/my-requests")
public class CustomerRequestController {

    private final CustomerRequestService requestService;

    public CustomerRequestController(CustomerRequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerRequestSummaryDTO>> getMyRequests(Authentication authentication) {
        return ResponseEntity.ok(requestService.getCustomerRequests(authentication));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerRequestDetailDTO> getMyRequestById(@PathVariable Long id,
                                                                     Authentication authentication) {
        return ResponseEntity.ok(requestService.getCustomerRequestById(id, authentication));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<CustomerRequestDetailDTO> cancelMyRequest(
            @PathVariable Long id,
            @RequestBody(required = false) java.util.Map<String, String> body,
            Authentication authentication) {
        String reason = (body != null) ? body.get("reason") : null;
        return ResponseEntity.ok(requestService.cancelCustomerRequest(id, reason, authentication));
    }
}
