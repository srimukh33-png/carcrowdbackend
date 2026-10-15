package com.carcrowd.controller;

import com.carcrowd.dto.admin.DashboardMetricsDTO;
import com.carcrowd.service.AdminDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    public AdminDashboardController(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardMetricsDTO> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboardMetrics());
    }
}
