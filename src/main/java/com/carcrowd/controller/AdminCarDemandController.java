package com.carcrowd.controller;

import com.carcrowd.dto.admin.CarDemandDTO;
import com.carcrowd.dto.admin.LeadSummaryDTO;
import com.carcrowd.service.AdminCarDemandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cars")
public class AdminCarDemandController {

    private final AdminCarDemandService carDemandService;

    public AdminCarDemandController(AdminCarDemandService carDemandService) {
        this.carDemandService = carDemandService;
    }

    @GetMapping("/demand")
    public ResponseEntity<List<CarDemandDTO>> getCarDemand(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String sortBy
    ) {
        return ResponseEntity.ok(carDemandService.getCarDemandAnalytics(brand, model, city, sortBy));
    }

    @GetMapping("/{carId}/leads")
    public ResponseEntity<List<LeadSummaryDTO>> getLeadsForCar(@PathVariable Long carId) {
        return ResponseEntity.ok(carDemandService.getLeadsForCar(carId));
    }
}
