package com.carcrowd.controller;

import com.carcrowd.dto.InterestRequestDTO;
import com.carcrowd.dto.InterestResponseDTO;
import com.carcrowd.service.InterestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cars/{carId}/interests")
public class InterestController {

    private final InterestService interestService;

    public InterestController(InterestService interestService) {
        this.interestService = interestService;
    }

    @PostMapping
    public ResponseEntity<InterestResponseDTO> registerInterest(
        @PathVariable Long carId,
        @Valid @RequestBody InterestRequestDTO request,
        org.springframework.security.core.Authentication authentication
    ) {
        InterestResponseDTO response = interestService.registerInterest(carId, request, authentication);
        return new ResponseEntity<>(response, response.isDuplicate() ? HttpStatus.OK : HttpStatus.CREATED);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getInterestCount(@PathVariable Long carId) {
        long count = interestService.getInterestCount(carId);
        return ResponseEntity.ok(Map.of(
            "carId", carId,
            "count", count
        ));
    }
}
