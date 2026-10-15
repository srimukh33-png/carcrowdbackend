package com.carcrowd.controller;

import com.carcrowd.dto.CarDetailDTO;
import com.carcrowd.dto.CarSummaryDTO;
import com.carcrowd.service.CarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public ResponseEntity<List<CarSummaryDTO>> getCars(@RequestParam(required = false) Long brandId) {
        return ResponseEntity.ok(carService.getCars(brandId));
    }

    @GetMapping("/{carId}")
    public ResponseEntity<CarDetailDTO> getCarById(@PathVariable Long carId) {
        return ResponseEntity.ok(carService.getCarDetail(carId));
    }
}
