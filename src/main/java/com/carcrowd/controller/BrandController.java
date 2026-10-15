package com.carcrowd.controller;

import com.carcrowd.dto.BrandDTO;
import com.carcrowd.dto.CarSummaryDTO;
import com.carcrowd.service.BrandService;
import com.carcrowd.service.CarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    private final BrandService brandService;
    private final CarService carService;

    public BrandController(BrandService brandService, CarService carService) {
        this.brandService = brandService;
        this.carService = carService;
    }

    @GetMapping
    public ResponseEntity<List<BrandDTO>> getActiveBrands() {
        return ResponseEntity.ok(brandService.getActiveBrands());
    }

    @GetMapping("/{brandId}")
    public ResponseEntity<BrandDTO> getBrandById(@PathVariable Long brandId) {
        return ResponseEntity.ok(brandService.getBrandById(brandId));
    }

    @GetMapping("/{brandId}/cars")
    public ResponseEntity<List<CarSummaryDTO>> getBrandCars(@PathVariable Long brandId) {
        // Validates that the brand exists before returning its cars
        brandService.getBrandById(brandId);
        return ResponseEntity.ok(carService.getCars(brandId));
    }
}
