package com.carcrowd.controller;

import com.carcrowd.dto.BrandDTO;
import com.carcrowd.dto.admin.CarAdminDTO;
import com.carcrowd.dto.admin.CreateCarRequest;
import com.carcrowd.dto.admin.DeleteCarResponse;
import com.carcrowd.dto.admin.UpdateCarRequest;
import com.carcrowd.exception.UnauthorizedException;
import com.carcrowd.service.AdminCarService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCarController {

    private final AdminCarService adminCarService;
    private final com.carcrowd.config.DatabaseCarCatalogInitializer catalogInitializer;

    public AdminCarController(AdminCarService adminCarService, com.carcrowd.config.DatabaseCarCatalogInitializer catalogInitializer) {
        this.adminCarService = adminCarService;
        this.catalogInitializer = catalogInitializer;
    }

    @PostMapping("/cars/sync-catalog")
    public ResponseEntity<Map<String, Object>> syncCatalog() {
        return ResponseEntity.ok(catalogInitializer.syncCatalogue());
    }

    private String requireAdminEmail(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new UnauthorizedException("Full administrative authentication required");
        }
        return authentication.getName();
    }

    @GetMapping("/cars")
    public ResponseEntity<Page<CarAdminDTO>> getCars(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        return ResponseEntity.ok(adminCarService.getCars(search, brandId, active, page, size, sortBy, sortDirection));
    }

    @GetMapping("/cars/{id:\\d+}")
    public ResponseEntity<CarAdminDTO> getCarById(@PathVariable Long id) {
        return ResponseEntity.ok(adminCarService.getCarById(id));
    }

    @PostMapping("/cars")
    public ResponseEntity<CarAdminDTO> createCar(
            @Valid @RequestBody CreateCarRequest request,
            Authentication authentication
    ) {
        String adminEmail = requireAdminEmail(authentication);
        CarAdminDTO created = adminCarService.createCar(request, adminEmail);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/cars/{id:\\d+}")
    public ResponseEntity<CarAdminDTO> updateCar(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCarRequest request,
            Authentication authentication
    ) {
        String adminEmail = requireAdminEmail(authentication);
        return ResponseEntity.ok(adminCarService.updateCar(id, request, adminEmail));
    }

    @PatchMapping("/cars/{id:\\d+}/status")
    public ResponseEntity<CarAdminDTO> updateCarStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> payload,
            Authentication authentication
    ) {
        String adminEmail = requireAdminEmail(authentication);
        boolean active = payload != null && Boolean.TRUE.equals(payload.get("active"));
        return ResponseEntity.ok(adminCarService.updateCarStatus(id, active, adminEmail));
    }

    @DeleteMapping("/cars/{id:\\d+}")
    public ResponseEntity<DeleteCarResponse> deleteCar(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String adminEmail = requireAdminEmail(authentication);
        return ResponseEntity.ok(adminCarService.deleteCar(id, adminEmail));
    }

    @GetMapping("/brands")
    public ResponseEntity<List<BrandDTO>> getAdminBrands() {
        return ResponseEntity.ok(adminCarService.getAllBrands());
    }
}
