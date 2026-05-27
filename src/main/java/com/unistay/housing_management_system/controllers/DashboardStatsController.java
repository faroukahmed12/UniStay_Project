package com.unistay.housing_management_system.controllers;

import com.unistay.housing_management_system.dtos.response.DashboardRequestCountsResponseDto;
import com.unistay.housing_management_system.services.DashboardStatsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardStatsController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardStatsController.class);

    private final DashboardStatsService dashboardStatsService;

    @GetMapping("/request-counts")
    public ResponseEntity<DashboardRequestCountsResponseDto> getRequestCounts() {
        logger.info("GET /api/dashboard/request-counts");
        return ResponseEntity.ok(dashboardStatsService.getRequestCounts());
    }
}

