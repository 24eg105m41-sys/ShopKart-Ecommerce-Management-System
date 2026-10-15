package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard/summary")
    public ResponseEntity<ApiResponse> getSummary() {
        return ResponseEntity.ok(ApiResponse.builder().success(true).message("Dashboard summary fetched successfully").data(dashboardService.getSummary()).build());
    }

    @GetMapping("/dashboard/sales-overview")
    public ResponseEntity<ApiResponse> getSalesOverview() {
        return ResponseEntity.ok(ApiResponse.builder().success(true).message("Sales overview fetched successfully").data(dashboardService.getSalesOverview()).build());
    }
}
