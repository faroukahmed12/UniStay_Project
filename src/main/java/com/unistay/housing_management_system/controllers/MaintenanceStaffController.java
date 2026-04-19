package com.unistay.housing_management_system.controllers;

import com.unistay.housing_management_system.dtos.response.MaintenanceStaffDto;
import com.unistay.housing_management_system.services.MaintenanceStaffService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance-staff")
@RequiredArgsConstructor
public class MaintenanceStaffController {

    private static final Logger logger = LoggerFactory.getLogger(MaintenanceStaffController.class);

    private final MaintenanceStaffService maintenanceStaffService;

    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MaintenanceStaffDto>> getAllStaff(
            @RequestParam(required = false) String specialization) {
        logger.info("GET /api/maintenance-staff — specialization filter: {}", specialization);

        List<MaintenanceStaffDto> result = (specialization != null && !specialization.isBlank())
                ? maintenanceStaffService.getStaffBySpecialization(specialization)
                : maintenanceStaffService.getAllStaff();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MaintenanceStaffDto> getStaffById(@PathVariable Long id) {
        logger.info("GET /api/maintenance-staff/{}", id);
        return ResponseEntity.ok(maintenanceStaffService.getStaffDtoById(id));
    }

    @PatchMapping("/{id}/deactivate")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateStaff(@PathVariable Long id) {
        logger.info("PATCH /api/maintenance-staff/{}/deactivate", id);
        maintenanceStaffService.deactivateStaff(id);
        return ResponseEntity.noContent().build();
    }
}
